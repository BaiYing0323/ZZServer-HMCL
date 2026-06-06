/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2021  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.jackhuang.hmcl;

import org.jackhuang.hmcl.util.StringUtils;
import org.jackhuang.hmcl.util.io.JarUtils;
import org.jackhuang.hmcl.util.platform.Architecture;
import org.jackhuang.hmcl.util.platform.OperatingSystem;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.EnumSet;

/**
 * Stores metadata about this application.
 */
public final class Metadata {
    private Metadata() {
    }

    // 客户端核心名称（所有界面自动同步）
    public static final String NAME = "ZZServer";
    public static final String FULL_NAME = "常平中学我的世界特供服务器";
    public static final String VERSION = System.getProperty("hmcl.version.override", JarUtils.getAttribute("hmcl.version", "@develop@"));

    // 窗口标题、关于界面、日志开头
    public static final String TITLE = FULL_NAME + " " + VERSION;
    public static final String FULL_TITLE = FULL_NAME + " v" + VERSION;

    // Java 版本要求（保持原版不变，保证兼容性）
    public static final int MINIMUM_REQUIRED_JAVA_VERSION = 17;
    public static final int MINIMUM_SUPPORTED_JAVA_VERSION = 17;
    public static final int RECOMMENDED_JAVA_VERSION = 21;

    // 官方链接（已替换为枕梦服务器地址）
    public static final String PUBLISH_URL = "https://www.pillowdream.cn/";
    public static final String DOWNLOAD_URL = PUBLISH_URL;
    // 彻底禁用所有更新功能
    public static final String HMCL_UPDATE_URL = "";
    public static final String MANUAL_UPDATE_URL = "";

    // 帮助文档链接
    public static final String DOCS_URL = "https://docs.pillowdream.cn/";
    public static final String CONTACT_URL = DOCS_URL;
    // 禁用不需要的官方链接
    public static final String CHANGELOG_URL = "";
    public static final String EULA_URL = "";
    public static final String GROUPS_URL = "";

    public static final String BUILD_CHANNEL = JarUtils.getAttribute("hmcl.version.type", "stable");
    public static final String GITHUB_SHA = JarUtils.getAttribute("hmcl.version.hash", null);

    // 目录配置（保持原版不变，保证兼容性）
    public static final Path CURRENT_DIRECTORY = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
    public static final Path MINECRAFT_DIRECTORY = OperatingSystem.getWorkingDirectory("minecraft");
    public static final Path HMCL_GLOBAL_DIRECTORY;
    public static final Path HMCL_CURRENT_DIRECTORY;
    public static final Path DEPENDENCIES_DIRECTORY;

    static {
        String hmclHome = System.getProperty("hmcl.home", System.getenv("HMCL_USER_HOME"));
        if (StringUtils.isBlank(hmclHome)) {
            if (OperatingSystem.CURRENT_OS.isLinuxOrBSD()) {
                String xdgData = System.getenv("XDG_DATA_HOME");
                if (StringUtils.isNotBlank(xdgData)) {
                    HMCL_GLOBAL_DIRECTORY = Path.of(xdgData, "zzserver").toAbsolutePath().normalize();
                } else {
                    HMCL_GLOBAL_DIRECTORY = Path.of(System.getProperty("user.home"), ".local", "share", "zzserver").toAbsolutePath().normalize();
                }
            } else {
                HMCL_GLOBAL_DIRECTORY = OperatingSystem.getWorkingDirectory("zzserver");
            }
        } else {
            HMCL_GLOBAL_DIRECTORY = Path.of(hmclHome).toAbsolutePath().normalize();
        }

        String hmclCurrentDir = System.getProperty("hmcl.dir", System.getenv("HMCL_LOCAL_HOME"));
        HMCL_CURRENT_DIRECTORY = StringUtils.isNotBlank(hmclCurrentDir)
                ? Path.of(hmclCurrentDir).toAbsolutePath().normalize()
                : CURRENT_DIRECTORY.resolve(".zzserver");

        String hmclDependencies = System.getProperty("hmcl.dependencies.dir", System.getenv("HMCL_DEPENDENCIES_DIR"));
        DEPENDENCIES_DIRECTORY = StringUtils.isNotBlank(hmclDependencies)
                ? Path.of(hmclDependencies).toAbsolutePath().normalize()
                : HMCL_CURRENT_DIRECTORY.resolve("dependencies");
    }

    public static boolean isStable() {
        return "stable".equals(BUILD_CHANNEL);
    }

    public static boolean isDev() {
        return "dev".equals(BUILD_CHANNEL);
    }

    public static boolean isNightly() {
        return !isStable() && !isDev();
    }

    public static @Nullable String getSuggestedJavaDownloadLink() {
        if (OperatingSystem.CURRENT_OS == OperatingSystem.LINUX && Architecture.SYSTEM_ARCH == Architecture.LOONGARCH64_OW)
            return "https://www.loongnix.cn/zh/api/java/downloads-jdk21/index.html";
        else {
            EnumSet<Architecture> supportedArchitectures;
            if (OperatingSystem.CURRENT_OS == OperatingSystem.WINDOWS)
                supportedArchitectures = EnumSet.of(Architecture.X86_64, Architecture.X86, Architecture.ARM64);
            else if (OperatingSystem.CURRENT_OS == OperatingSystem.LINUX)
                supportedArchitectures = EnumSet.of(
                        Architecture.X86_64, Architecture.X86,
                        Architecture.ARM64, Architecture.ARM32,
                        Architecture.RISCV64, Architecture.LOONGARCH64
                );
            else if (OperatingSystem.CURRENT_OS == OperatingSystem.MACOS)
                supportedArchitectures = EnumSet.of(Architecture.X86_64, Architecture.ARM64);
            else
                supportedArchitectures = EnumSet.noneOf(Architecture.class);
            if (supportedArchitectures.contains(Architecture.SYSTEM_ARCH))
                // 禁用官方Java下载链接，指向枕梦官网
                return "https://www.pillowdream.cn/";
            else
                return null;
        }
    }
}