package com.xmq.xbind.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.xmq.xbind.util.Logger
import com.xmq.xbind.util.ScanUtil
import org.apache.commons.io.FileUtils

/**
 * @author xmqyeah* @CreateDate 2021/8/22 17:31
 */
class XBindTransform extends Transform {
    static HashMap<String, String> registers = new LinkedHashMap<>()
    static File fileToInitClass
    @Override
    String getName() {
        return "XBind"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        def startTime = System.currentTimeMillis()
        if (!transformInvocation.isIncremental()) {
            transformInvocation.getOutputProvider().deleteAll()
        }
        transformInvocation.inputs.each {transformInput ->
            transformInput.directoryInputs.each{ dirInput ->
                def sourceFile = dirInput.getFile();
                String root = sourceFile.absolutePath
                def destDir = transformInvocation.getOutputProvider().getContentLocation(
                        dirInput.getName(), dirInput.getContentTypes(), dirInput.getScopes(), Format.DIRECTORY
                )
                println("find dir: ${sourceFile.name}, $destDir == ${dirInput.changedFiles.entries}")
                scanDirectory(root, sourceFile)
                println("copyDirectory: ${dirInput.file.path} => ${destDir.path}")
                FileUtils.copyDirectory(sourceFile, destDir)
            }
            transformInput.getJarInputs().each {jarInput ->
                def dest = transformInvocation.getOutputProvider().getContentLocation(
                        jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR
                )
                if (ScanUtil.shouldProcessPreDexJar(jarInput.file.absolutePath)) {
                    ScanUtil.scanJar(jarInput.file, dest)
                }
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        Logger.info('Scan finish, current cost time ' + (System.currentTimeMillis() - startTime)
                + "ms:\n\tfileToInitClass: $fileToInitClass")

        if (fileToInitClass) {
            Logger.info('Insert registers ' + registers)
            registers.each { ext ->
                Logger.info('Insert register code to file ' + fileToInitClass.absolutePath)

//                if (ext.classList.isEmpty()) {
//                    Logger.e("No class implements found for interface:" + ext.interfaceName)
//                } else {
                    registers.each { key, value->
                        Logger.info("Register: $key, $value")
                    }
                    CodeGenerator.insertInitCodeTo()
//                }
            }
        }
    }

    void scanDirectory(String root, File sourceFile) {
        boolean leftSlash = File.separator == '/'
        sourceFile.listFiles(new FileFilter() {
            @Override
            boolean accept(File file) {
                return (file != null && (file.isDirectory() || file.name.endsWith(".class")))
            }
        }).each {file->
            try {
                if (file.isDirectory()) {
                    scanDirectory(root, file)
                } else if (file.name.endsWith(".class")) {
                    if (!file.name.contains("BuildConfig") && !file.name.startsWith("R\$")) {
                        def path = file.absolutePath.replace(root, '')
                        if (!leftSlash) {
                            path = path.replaceAll("\\\\", "/")
                        }
                        if (ScanUtil.shouldProcessClass(path)) {
                            ScanUtil.scanClass(file)
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace()
            }
        }
    }

}
