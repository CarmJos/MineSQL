package cc.carm.plugin.easysql;

import cc.carm.lib.githubreleases4j.GithubReleases4J;

import java.util.logging.Logger;

public class UpdateChecker {

    public static final String REPO_OWNER = "CarmJos";
    public static final String REPO_NAME = "EasySQL-Plugin";

    public void checkUpdate(Logger logger, String currentVersion) {
        Integer behindVersions = GithubReleases4J.getVersionBehind(REPO_OWNER, REPO_NAME, currentVersion);
        String downloadURL = GithubReleases4J.getReleasesURL(REPO_OWNER, REPO_NAME);
        if (behindVersions == null) {
            logger.severe("检查更新失败，请您定期查看插件是否更新，避免安全问题。");
            logger.severe("下载地址 " + downloadURL);
        } else if (behindVersions < 0) {
            logger.severe("检查更新失败! 当前版本未知，请您使用原生版本以避免安全问题。");
            logger.severe("最新版下载地址 " + downloadURL);
        } else if (behindVersions > 0) {
            logger.warning("发现新版本! 目前已落后 " + behindVersions + " 个版本。");
            logger.warning("最新版下载地址 " + downloadURL);
        } else {
            logger.info("检查完成，当前已是最新版本。");
        }
    }

}
