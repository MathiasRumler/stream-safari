package mvp.streamy.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

@Service
@Slf4j
public class StreamPipelineEngineServiceV2 {

    public <T> Object execute(
            List<?> input,
            String pipeline,
            Class<T> elementType
    ) {
        validatePipeline(pipeline);

        String typeImport = getTypeImport(elementType);
        String typeName = elementType.getSimpleName();

        Map<String, String> sources = new HashMap<>();
        sources.put(
                "demo.StreamProgram",
                generateSource(pipeline, typeImport, typeName)
        );

        CompilationResult result = compile(sources);

        if (!result.success) {
            StringBuilder sb = new StringBuilder();
            for (Diagnostic<? extends JavaFileObject> d : result.diagnostics) {
                sb.append(d.getMessage(null));
            }
            throw new IllegalArgumentException(sb.toString());
        }

        try {
            ClassLoader isolatedLoader =
                    new IsolatedClassLoader(
                            result.compiledClasses,
                            ClassLoader.getSystemClassLoader()
                    );

            Class<?> programClass =
                    isolatedLoader.loadClass("demo.StreamProgram");

            Method runMethod =
                    programClass.getMethod("run", List.class);

            @SuppressWarnings("unchecked")
            Object output =
                     runMethod.invoke(null, input);

            return output;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getTypeImport(Class<?> type) {
        // Only add import if it's not in java.lang package
        if (type.getPackage() != null &&
                !type.getPackage().getName().equals("java.lang")) {
            return "import " + type.getName() + ";\n";
        }
        return "";
    }

    private void validatePipeline(String pipeline) {
        Set<String> forbidden =
                Set.of(
                        "parallel",
                        "forEach",
                        "peek",
                        "new ",
                        "System",
                        "Runtime",
                        "Thread"
                );
        for (String token : forbidden) {
            if (pipeline.contains(token)) {
                throw new IllegalArgumentException(
                        "Forbidden construct detected: " + token
                );
            }
        }
                if (!pipeline.trim().startsWith(".")) {
            throw new IllegalArgumentException(
                    "Pipeline must start with a method call (.)"
            );
        }
    }

    private String generateSource(String pipeline, String typeImport, String typeName) {
        return """
            package demo;

            import java.util.List;
            import java.util.stream.Collectors;
            import java.util.Optional;
            import java.util.Comparator;
            %s

            public class StreamProgram {

                public static Object run(List<%s> input) {
                    return input.stream()
                            %s
                            ;
                }
            }
            """.formatted(typeImport, typeName, pipeline);
    }


    private CompilationResult compile(Map<String, String> sources) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Compiler not available");
        }

        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<>();

        StandardJavaFileManager standardFileManager =
                compiler.getStandardFileManager(diagnostics, null, null);

        MemoryFileManager fileManager =
                new MemoryFileManager(standardFileManager);

        List<JavaFileObject> sourceFiles = new ArrayList<>();

        for (Map.Entry<String, String> e : sources.entrySet()) {
            sourceFiles.add(new SourceCode(e.getKey(), e.getValue()));
        }

        JavaCompiler.CompilationTask task =
                compiler.getTask(
                        null,
                        fileManager,
                        diagnostics,
                        null,
                        null,
                        sourceFiles
                );

        boolean success = Boolean.TRUE.equals(task.call());

        return new CompilationResult(
                success,
                diagnostics.getDiagnostics(),
                fileManager.getAllByteCode()
        );
    }



    private static class CompilationResult {
        final boolean success;
        final List<Diagnostic<? extends JavaFileObject>> diagnostics;
        final Map<String, byte[]> compiledClasses;

        CompilationResult(
                boolean success,
                List<Diagnostic<? extends JavaFileObject>> diagnostics,
                Map<String, byte[]> compiledClasses
        ) {
            this.success = success;
            this.diagnostics = diagnostics;
            this.compiledClasses = compiledClasses;
        }
    }

    private static class SourceCode extends SimpleJavaFileObject {
        private final String code;

        SourceCode(String className, String code) {
            super(
                    URI.create(
                            "string:///" +
                                    className.replace('.', '/') +
                                    Kind.SOURCE.extension
                    ),
                    Kind.SOURCE
            );
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    private static class ByteCode extends SimpleJavaFileObject {
        private final ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        ByteCode(String className) {
            super(
                    URI.create(
                            "bytes:///" +
                                    className.replace('.', '/') +
                                    Kind.CLASS.extension
                    ),
                    Kind.CLASS
            );
        }

        @Override
        public OutputStream openOutputStream() {
            return outputStream;
        }

        byte[] getBytes() {
            return outputStream.toByteArray();
        }
    }

    private static class MemoryFileManager
            extends ForwardingJavaFileManager<StandardJavaFileManager> {

        private final Map<String, ByteCode> compiled =
                new HashMap<>();

        MemoryFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                JavaFileManager.Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling
        ) {
            ByteCode byteCode = new ByteCode(className);
            compiled.put(className, byteCode);
            return byteCode;
        }

        Map<String, byte[]> getAllByteCode() {
            Map<String, byte[]> out = new HashMap<>();
            for (Map.Entry<String, ByteCode> e : compiled.entrySet()) {
                out.put(e.getKey(), e.getValue().getBytes());
            }
            return out;
        }
    }

    private static class IsolatedClassLoader extends ClassLoader {
        private final Map<String, byte[]> classes;

        IsolatedClassLoader(
                Map<String, byte[]> classes,
                ClassLoader parent
        ) {
            super(parent);
            this.classes = classes;
        }

        @Override
        protected Class<?> findClass(String name)
                throws ClassNotFoundException {

            byte[] bytes = classes.get(name);
            if (bytes == null) {
                return super.findClass(name);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}