package com.example.renamer;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class FileRenamer {

    private final Context context;
    private final Random random = new SecureRandom();

    // ============ 词库 ============
    private static final String[] WORDS = {
        // 公司/品牌 (60)
        "google", "microsoft", "apple", "amazon", "facebook", "twitter", "instagram",
        "whatsapp", "telegram", "signal", "zoom", "slack", "discord", "reddit",
        "netflix", "spotify", "uber", "airbnb", "dropbox", "box", "salesforce",
        "oracle", "ibm", "hp", "dell", "cisco", "vmware", "sap", "adobe", "autodesk",
        "nokia", "ericsson", "motorola", "lg", "sony", "panasonic", "philips",
        "samsung", "xiaomi", "huawei", "oppo", "vivo", "zte", "lenovo", "asus",
        "acer", "gigabyte", "msi", "coolpad", "oneplus", "blackberry", "htc",
        "sharp", "fujitsu", "nec", "olympus", "canon", "nikon", "fujifilm",

        // 中国公司 (30)
        "alibaba", "tencent", "baidu", "huawei", "xiaomi", "oppo", "vivo", "meizu",
        "oneplus", "zte", "lenovo", "asus", "acer", "gigabyte", "msi", "coolpad",
        "gionee", "hisense", "tcl", "skyworth", "konka", "changhong", "haier",
        "gree", "midea", "galanz", "supor", "joyoung", "roborock", "ecovacs",

        // 系统/技术 (60)
        "android", "ios", "windows", "linux", "unix", "macos", "tvos", "watchos",
        "kernel", "driver", "firmware", "bootloader", "recovery", "ota", "adb",
        "fastboot", "twrp", "magisk", "supersu", "busybox", "terminal",
        "shell", "bash", "zsh", "fish", "ssh", "ssl", "tls", "https", "http", "ftp",
        "smtp", "pop3", "imap", "dns", "dhcp", "nfs", "samba", "ldap", "kerberos",
        "nfc", "bluetooth", "wifi", "5g", "4g", "lte", "gps", "glonass", "galileo",
        "beidou", "zigbee", "thread", "matter", "homekit", "alexa", "assistant",
        "siri", "bixby", "cortana", "heygoogle",

        // 编程语言 (50)
        "java", "kotlin", "python", "ruby", "php", "swift", "rust", "go", "golang",
        "cplusplus", "csharp", "javascript", "typescript", "html", "css", "scss",
        "less", "sql", "plsql", "mongodb", "redis", "elasticsearch", "graphql",
        "rest", "soap", "xml", "json", "yaml", "toml", "protobuf",
        "dart", "flutter", "react", "vue", "angular", "svelte", "solidjs",
        "qwik", "alpine", "stimulus", "htmx", "wasm", "assembly", "fortran",
        "pascal", "ada", "lisp", "scheme", "clojure", "elixir", "erlang",

        // 框架/库 (50)
        "spring", "hibernate", "mybatis", "struts", "react", "vue", "angular",
        "jquery", "bootstrap", "tailwind", "flutter", "reactnative", "xamarin",
        "cocos", "unity", "unreal", "godot", "opencv", "tensorflow", "pytorch",
        "keras", "scikit", "numpy", "pandas", "matplotlib", "django", "flask",
        "fastapi", "rails", "laravel", "symfony", "codeigniter", "cakephp",
        "phoenix", "elixir", "clojure", "groovy", "scala", "haskell", "erlang",
        "nodejs", "deno", "bun", "express", "nestjs", "nextjs", "nuxtjs",

        // 数据库 (40)
        "mysql", "postgresql", "oracle", "sqlite", "mongodb", "cassandra",
        "redis", "memcached", "elasticsearch", "solr", "clickhouse", "doris",
        "tidb", "oceanbase", "polardb", "gaussdb", "tair", "hbase", "hive", "spark",
        "flink", "kafka", "pulsar", "rabbitmq", "rocketmq", "activemq", "zeromq",
        "influxdb", "prometheus", "grafana", "victoriametrics", "thanos", "cortex",
        "loki", "tempo", "phlare", "mimir", "alertmanager", "zookeeper", "etcd", "consul",

        // 云/服务器 (40)
        "aws", "azure", "gcp", "aliyun", "tencentcloud", "baiducloud", "huaweicloud",
        "digitalocean", "linode", "vultr", "cloudflare", "fastly", "akamai",
        "nginx", "apache", "tomcat", "jetty", "undertow", "wildfly", "weblogic",
        "websphere", "jboss", "glassfish", "resin", "lighttpd", "caddy", "traefik",
        "envoy", "haproxy", "varnish", "squid", "dnsmasq", "bind", "unbound", "powerdns",
        "minio", "ceph", "glusterfs", "longhorn", "rancher", "k3s", "k8s", "openshift",

        // 加密/安全 (40)
        "crypto", "aes", "rsa", "ecc", "sha256", "md5", "base64", "hex", "binary",
        "encrypt", "decrypt", "signature", "certificate", "firewall", "antivirus",
        "malware", "ransomware", "phishing", "spam", "hack", "exploit", "vulnerability",
        "patch", "update", "hotfix", "backdoor", "trojan", "worm", "virus", "rootkit",
        "keylogger", "adware", "spyware", "scareware", "cryptojacking", "blockchain",
        "wallet", "mining", "hashrate", "difficulty", "nonce", "merkle", "oracle",

        // 游戏/娱乐 (60)
        "game", "play", "music", "video", "movie", "tv", "show", "song", "album",
        "podcast", "stream", "broadcast", "live", "vod", "drama", "comedy",
        "action", "adventure", "rpg", "fps", "mmo", "moba", "card", "puzzle",
        "racing", "sports", "simulation", "strategy", "board", "arcade",
        "minecraft", "fortnite", "valorant", "csgo", "dota", "lol", "wow",
        "overwatch", "diablo", "starcraft", "warcraft", "hearthstone", "pubg",
        "cod", "bf", "apex", "warzone", "destiny", "halo", "gears", "uncharted",
        "godofwar", "horizon", "zelda", "mario", "pokemon", "sonic", "pacman",

        // 通用词 (80)
        "system", "core", "lib", "data", "cache", "temp", "tmp", "log", "cfg", "conf",
        "config", "settings", "preferences", "profile", "user", "admin", "guest",
        "root", "home", "dev", "prod", "test", "stage", "beta", "alpha", "daily",
        "nightly", "release", "stable", "unstable", "legacy", "modern", "classic",
        "basic", "advanced", "pro", "max", "ultra", "premium", "deluxe", "lite",
        "nano", "micro", "mini", "mega", "giga", "tera", "peta", "exa", "zetta",
        "infinity", "eternity", "forever", "never", "always", "ever", "soon",
        "later", "now", "then", "once", "twice", "thrice", "final", "initial", "first",
        "last", "next", "prev", "current", "default", "custom", "new", "old",
        "active", "inactive", "pending", "complete", "running", "stopped", "ready",
        "unknown", "known", "visible", "hidden", "normal", "emergency", "critical",
        "info", "debug", "trace", "error", "fatal", "warn", "success", "fail"
    };

    // ============ 后缀列表 ============
    private static final String[] EXTENSIONS = {
        ".cache", ".dll", ".bin", ".dat", ".tmp", ".log", ".sys", ".core", 
        ".ota", ".idx", ".vdex", ".odex", ".jar", ".so", ".xml", ".json", 
        ".db", ".cfg", ".ini", ".yaml", ".toml", ".properties", ".conf",
        ".wmv", ".mp4", ".avi", ".mkv", ".mov", ".flv", ".webm", ".m4v",
        ".mp3", ".wav", ".flac", ".aac", ".ogg", ".wma", ".m4a",
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg", ".tiff",
        ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt",
        ".md", ".rtf", ".odt", ".ods", ".odp",
        ".zip", ".rar", ".7z", ".gz", ".tar", ".iso", ".img", ".xz",
        ".bz2", ".tgz", ".zst",
        ".apk", ".aab", ".dex", ".class", ".java", ".kt", ".dart",
        ".html", ".htm", ".css", ".scss", ".js", ".ts", ".jsx", ".tsx",
        ".php", ".jsp", ".asp", ".aspx",
        ".c", ".cpp", ".h", ".hpp", ".py", ".rb", ".go", ".rs", ".swift",
        ".pl", ".pm", ".sh", ".bash", ".zsh", ".fish"
    };

    public FileRenamer(Context context) {
        this.context = context.getApplicationContext();
    }

    // 整个文件夹重命名（保留，兼容旧版）
    public int renameFiles(Uri treeUri) {
        if (treeUri == null) {
            return 0;
        }
        DocumentFile dir = DocumentFile.fromTreeUri(context, treeUri);
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return 0;
        }
        int count = 0;
        for (DocumentFile file : dir.listFiles()) {
            if (renameOne(file)) {
                count++;
            }
        }
        return count;
    }

    // 多选文件重命名（新增）
    public int renameFiles(List<Uri> fileUris) {
        if (fileUris == null || fileUris.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Uri uri : fileUris) {
            DocumentFile file = DocumentFile.fromSingleUri(context, uri);
            if (renameOne(file)) {
                count++;
            }
        }
        return count;
    }

    private boolean renameOne(DocumentFile file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        String oldName = file.getName();
        if (oldName == null) {
            return false;
        }
        // 保留原扩展名
        String extension = "";
        int dotIndex = oldName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < oldName.length() - 1) {
            extension = oldName.substring(dotIndex);
        }
        String newName = generateRandomFileName(extension);
        boolean success;
        try {
            success = file.renameTo(newName);
        } catch (Exception e) {
            success = false;
        }

        if (!success) {
            // DocumentFile 标准 rename 不支持时（常见于"最近"页面选出的 MediaStore 文件），
            // 降级走 MediaStore 的 DISPLAY_NAME 更新接口再试一次。
            success = renameViaMediaStore(file.getUri(), newName);
        }
        if (success) {
            appendLog(oldName, newName);
        }
        return success;
    }

    private String generateRandomFileName(String extension) {
        int format = random.nextInt(8);
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        long ts = System.currentTimeMillis();

        switch (format) {
            case 0:
                return randomWord() + "." + randomWord() + "." + 
                       randomWord() + "." + randomWord() + extension;
            case 1:
                return randomWord() + "_" + date + "_" + randomAlphanumeric(4) + extension;
            case 2:
                return randomWord() + "_" + randomWord() + "_" + 
                       randomWord() + "_" + ts + extension;
            case 3:
                return randomWord() + "-" + randomWord() + "-" + 
                       randomWord() + "-" + randomAlphanumeric(4) + extension;
            case 4:
                return date + "_" + randomWord() + "_" + 
                       randomWord() + "_" + randomWord() + extension;
            case 5:
                return randomWord() + "." + randomWord() + "." + 
                       randomWord() + "." + randomAlphanumeric(4) + extension;
            case 6:
                return randomWord() + "_" + ts + "_" + randomAlphanumeric(4) + extension;
            default:
                return randomWord() + "_" + ts + "_" + randomWord() + extension;
        }
    }

    private String randomWord() {
        return WORDS[random.nextInt(WORDS.length)];
    }

    private String randomAlphanumeric(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void appendLog(String oldName, String newName) {
        File logFile = new File(context.getFilesDir(), "rename_log.txt");
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        String line = timestamp + "\t" + oldName + " -> " + newName + "\n";
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(line);
        } catch (IOException ignored) {
        }
    }
}
