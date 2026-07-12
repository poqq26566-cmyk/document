package com.example.renamer;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

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

    public Exception pendingSecurityException;

    // ============ 词库 ============
    private static final String[] WORDS = {
        "google", "microsoft", "apple", "amazon", "facebook", "twitter", "instagram",
        "whatsapp", "telegram", "signal", "zoom", "slack", "discord", "reddit",
        "netflix", "spotify", "uber", "airbnb", "dropbox", "box", "salesforce",
        "oracle", "ibm", "hp", "dell", "cisco", "vmware", "sap", "adobe", "autodesk",
        "nokia", "ericsson", "motorola", "lg", "sony", "panasonic", "philips",
        "samsung", "xiaomi", "huawei", "oppo", "vivo", "zte", "lenovo", "asus",
        "acer", "gigabyte", "msi", "coolpad", "oneplus", "blackberry", "htc",
        "sharp", "fujitsu", "nec", "olympus", "canon", "nikon", "fujifilm",
        "alibaba", "tencent", "baidu", "meizu", "gionee", "hisense", "tcl",
        "skyworth", "konka", "changhong", "haier", "gree", "midea", "galanz",
        "supor", "joyoung", "roborock", "ecovacs", "dji", "xiaopeng", "nio",
        "geely", "chery", "byd", "greatwall", "changan", "dongfeng",
        "android", "ios", "windows", "linux", "unix", "macos", "tvos", "watchos",
        "kernel", "driver", "firmware", "bootloader", "recovery", "ota", "adb",
        "fastboot", "twrp", "magisk", "supersu", "busybox", "terminal",
        "shell", "bash", "zsh", "fish", "ssh", "ssl", "tls", "https", "http", "ftp",
        "smtp", "pop3", "imap", "dns", "dhcp", "nfs", "samba", "ldap", "kerberos",
        "nfc", "bluetooth", "wifi", "5g", "4g", "lte", "gps", "glonass", "galileo",
        "beidou", "zigbee", "thread", "matter", "homekit", "alexa", "assistant",
        "siri", "bixby", "cortana", "heygoogle",
        "java", "kotlin", "python", "ruby", "php", "swift", "rust", "go", "golang",
        "cplusplus", "csharp", "javascript", "typescript", "html", "css", "scss",
        "less", "sql", "plsql", "mongodb", "redis", "elasticsearch", "graphql",
        "rest", "soap", "xml", "json", "yaml", "toml", "protobuf",
        "dart", "flutter", "react", "vue", "angular", "svelte", "solidjs",
        "qwik", "alpine", "stimulus", "htmx", "wasm", "assembly", "fortran",
        "pascal", "ada", "lisp", "scheme", "clojure", "elixir", "erlang",
        "spring", "hibernate", "mybatis", "struts", "react", "vue", "angular",
        "jquery", "bootstrap", "tailwind", "flutter", "reactnative", "xamarin",
        "cocos", "unity", "unreal", "godot", "opencv", "tensorflow", "pytorch",
        "keras", "scikit", "numpy", "pandas", "matplotlib", "django", "flask",
        "fastapi", "rails", "laravel", "symfony", "codeigniter", "cakephp",
        "phoenix", "groovy", "scala", "haskell", "nodejs", "deno", "bun",
        "express", "nestjs", "nextjs", "nuxtjs", "gatsby", "remix", "swiftui",
        "mysql", "postgresql", "oracle", "sqlite", "mongodb", "cassandra",
        "redis", "memcached", "elasticsearch", "solr", "clickhouse", "doris",
        "tidb", "oceanbase", "polardb", "gaussdb", "tair", "hbase", "hive", "spark",
        "flink", "kafka", "pulsar", "rabbitmq", "rocketmq", "activemq", "zeromq",
        "influxdb", "prometheus", "grafana", "victoriametrics", "thanos", "cortex",
        "loki", "tempo", "mimir", "alertmanager", "zookeeper", "etcd", "consul",
        "aws", "azure", "gcp", "aliyun", "tencentcloud", "baiducloud", "huaweicloud",
        "digitalocean", "linode", "vultr", "cloudflare", "fastly", "akamai",
        "nginx", "apache", "tomcat", "jetty", "undertow", "wildfly", "weblogic",
        "websphere", "jboss", "glassfish", "resin", "lighttpd", "caddy", "traefik",
        "envoy", "haproxy", "varnish", "squid", "dnsmasq", "bind", "unbound", "powerdns",
        "minio", "ceph", "glusterfs", "longhorn", "rancher", "k3s", "k8s", "openshift",
        "crypto", "aes", "rsa", "ecc", "sha256", "md5", "base64", "hex", "binary",
        "encrypt", "decrypt", "signature", "certificate", "firewall", "antivirus",
        "malware", "ransomware", "phishing", "spam", "hack", "exploit", "vulnerability",
        "patch", "update", "hotfix", "backdoor", "trojan", "worm", "virus", "rootkit",
        "keylogger", "adware", "spyware", "scareware", "cryptojacking", "blockchain",
        "wallet", "mining", "hashrate", "difficulty", "nonce", "merkle",
        "game", "play", "music", "video", "movie", "tv", "show", "song", "album",
        "podcast", "stream", "broadcast", "live", "vod", "drama", "comedy",
        "action", "adventure", "rpg", "fps", "mmo", "moba", "card", "puzzle",
        "racing", "sports", "simulation", "strategy", "board", "arcade",
        "minecraft", "fortnite", "valorant", "csgo", "dota", "lol", "wow",
        "overwatch", "diablo", "starcraft", "warcraft", "hearthstone", "pubg",
        "cod", "bf", "apex", "warzone", "destiny", "halo", "gears", "uncharted",
        "godofwar", "horizon", "zelda", "mario", "pokemon", "sonic", "pacman",
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

    // ============ 扩展名列表 ============
    private static final String[] EXTENSIONS = {
        // 视频
        ".wmv", ".mp4", ".avi", ".mkv", ".mov", ".flv", ".webm", ".m4v",
        ".mpg", ".mpeg", ".3gp", ".mts", ".m2ts", ".ts", ".vob", ".ogv",
        // 音频
        ".mp3", ".wav", ".flac", ".aac", ".ogg", ".wma", ".m4a",
        // 图片
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg", ".tiff",
        ".tif", ".heic", ".heif", ".avif", ".raw", ".cr2", ".nef", ".arw",
        ".dng", ".orf", ".rw2", ".jfif", ".exr", ".hdr", ".pbm", ".pgm",
        ".ppm", ".xbm", ".xpm", ".ico", ".cur", ".ani",
        // 文档
        ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt",
        ".md", ".rtf", ".odt", ".ods", ".odp", ".odg", ".odf", ".csv",
        ".tsv", ".psd", ".ai", ".eps", ".cdr", ".xps", ".pages", ".numbers", ".key",
        ".tex", ".latex", ".bib", ".sty", ".cls", ".dtx", ".ins", ".idx",
        ".ilg", ".ind", ".ist",
        // 压缩
        ".zip", ".rar", ".7z", ".gz", ".tar", ".iso", ".img", ".xz",
        ".bz2", ".tgz", ".zst", ".lz", ".lz4", ".lzma", ".br",
        ".arj", ".cab", ".dmg", ".hqx", ".sit", ".sea", ".bin", ".cue", ".mdf",
        ".mds", ".nrg", ".vcd", ".cdi", ".bwi", ".bwt", ".ccd", ".sub",
        ".udf", ".hfs",
        // 移动端
        ".apk", ".aab", ".dex", ".class", ".java", ".kt", ".dart",
        ".odex", ".vdex", ".oat", ".art", ".scm", ".diff", ".patch",
        ".xapk", ".apks", ".apkm", ".obb",
        // Web
        ".html", ".htm", ".css", ".scss", ".js", ".ts", ".jsx", ".tsx",
        ".php", ".jsp", ".asp", ".aspx", ".wasm", ".webmanifest", ".wpt",
        ".vue", ".svelte", ".astro", ".liquid", ".hbs", ".ejs", ".pug",
        ".haml", ".slim", ".erbl",
        // 代码
        ".c", ".cpp", ".h", ".hpp", ".py", ".rb", ".go", ".rs", ".swift",
        ".pl", ".pm", ".sh", ".bash", ".zsh", ".fish", ".lua", ".r", ".m",
        ".mm", ".vim", ".el", ".erl", ".hrl", ".ex", ".exs",
        // 系统/配置文件
        ".cache", ".dll", ".bin", ".dat", ".tmp", ".log", ".sys", ".core",
        ".ota", ".idx", ".vdex", ".odex", ".jar", ".so", ".xml", ".json",
        ".db", ".cfg", ".ini", ".yaml", ".toml", ".properties", ".conf",
        ".lock", ".pid", ".socket", ".pipe", ".fifo", ".dev", ".null",
        ".ko", ".o", ".a", ".la", ".diag", ".manifest",
        // 安装包
        ".exe", ".msi", ".dmg", ".pkg", ".deb", ".rpm", ".pacman",
        ".nupkg", ".whl", ".egg", ".gem", ".war", ".ear",
        // 字体
        ".ttf", ".otf", ".woff", ".woff2", ".eot",
        // 证书
        ".cert", ".pem", ".key", ".crt", ".csr", ".jks", ".keystore", ".truststore",
        ".der", ".p12", ".pfx",
        // 3D
        ".blend", ".3ds", ".fbx", ".obj", ".stl", ".dae", ".x3d", ".vrml", ".wrl",
        ".glb", ".gltf",
        // 游戏
        ".game", ".arc", ".pak", ".sav", ".sol", ".rom", ".nes", ".smc", ".sfc",
        ".n64", ".z64", ".ps1", ".psv", ".xbe", ".xex", ".wad", ".pk3", ".pk4",
        ".bsp", ".vpk", ".gma", ".gs", ".mpq", ".sga", ".rpf", ".umap", ".uasset",
        ".uexp", ".prefab", ".unity", ".asset", ".meta", ".cs", ".mat", ".shader", ".cginc",
        // 虚拟机
        ".vmdk", ".vdi", ".qcow2", ".vhdx", ".ova", ".ovf", ".vbox", ".vmx", ".vmxf",
        // 其他
        ".torrent", ".part", ".crdownload", ".download", ".resume",
        ".bak", ".old", ".new", ".temp", ".sav", ".save",
        ".state", ".checkpoint", ".snapshot", ".srt", ".ass", ".ssa", ".sub",
        ".lrc", ".krc", ".vtt", ".ttml", ".dfxp",
        ".nfo", ".diz", ".readme", ".chm", ".hlp", ".cnt",
        ".gnupg", ".gpg", ".asc", ".sig", ".sign", ".sfv",
        ".md5", ".sha1", ".sha256", ".sha512"
    };

    public FileRenamer(Context context) {
        this.context = context.getApplicationContext();
    }

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

    public int renameFiles(List<DocumentFile> files, boolean isDocumentFileList) {
        if (files == null || files.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (DocumentFile file : files) {
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
        String newName = generateRandomFileName();
        boolean success;
        try {
            success = file.renameTo(newName);
        } catch (Exception e) {
            success = false;
        }
        if (!success) {
            success = renameViaMediaStore(file.getUri(), newName);
        }
        if (success) {
            appendLog(oldName, newName);
        }
        return success;
    }

    private String generateRandomFileName() {
        int format = random.nextInt(8);
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        long ts = System.currentTimeMillis();
        String ext = EXTENSIONS[random.nextInt(EXTENSIONS.length)];

        switch (format) {
            case 0:
                return randomWord() + "." + randomWord() + "." + 
                       randomWord() + "." + randomWord() + ext;
            case 1:
                return randomWord() + "_" + date + "_" + randomAlphanumeric(4) + ext;
            case 2:
                return randomWord() + "_" + randomWord() + "_" + 
                       randomWord() + "_" + ts + ext;
            case 3:
                return randomWord() + "-" + randomWord() + "-" + 
                       randomWord() + "-" + randomAlphanumeric(4) + ext;
            case 4:
                return date + "_" + randomWord() + "_" + 
                       randomWord() + "_" + randomWord() + ext;
            case 5:
                return randomWord() + "." + randomWord() + "." + 
                       randomWord() + "." + randomAlphanumeric(4) + ext;
            case 6:
                return randomWord() + "_" + ts + "_" + randomAlphanumeric(4) + ext;
            default:
                return randomWord() + "_" + ts + "_" + randomWord() + ext;
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

    private boolean renameViaMediaStore(Uri uri, String newName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, newName);
            int rows = context.getContentResolver().update(uri, values, null, null);
            return rows > 0;
        } catch (SecurityException e) {
            pendingSecurityException = e;
            return false;
        } catch (Exception e) {
            return false;
        }
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
