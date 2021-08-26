package com.xmq.xbind

import com.android.build.gradle.AppExtension
import com.xmq.xbind.core.XBindTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class XBindPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.logger.warn(">>XBindPlugin apply: ${project.name}");
        if (project == project.getRootProject()) {
            return
        }
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new XBindTransform())
    }
}