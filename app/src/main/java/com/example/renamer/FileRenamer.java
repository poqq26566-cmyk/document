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
import java.util.Locale;
import java.util.Random;

public class FileRenamer {

    private final Context context;
    private final Random random = new SecureRandom();

    // ============ 500+ 核心词库 ============
    private static final String[] WORDS = {
        // 公司/品牌 (30)
        "google", "microsoft", "apple", "amazon", "facebook", "twitter", "instagram",
        "whatsapp", "telegram", "signal", "zoom", "slack", "discord", "reddit",
        "netflix", "spotify", "uber", "airbnb", "dropbox", "box", "salesforce",
        "oracle", "ibm", "hp", "dell", "cisco", "vmware", "sap", "adobe", "autodesk",

        // 中国公司 (30)
        "alibaba", "tencent", "baidu", "huawei", "xiaomi", "oppo", "vivo", "meizu",
        "oneplus", "zte", "lenovo", "asus", "acer", "gigabyte", "msi", "coolpad",
        "gionee", "hisense", "tcl", "skyworth", "konka", "changhong", "haier",
        "gree", "midea", "galanz", "supor", "joyoung", "roborock", "ecovacs",

        // 系统/技术 (40)
        "android", "ios", "windows", "linux", "unix", "macos", "tvos", "watchos",
        "kernel", "driver", "firmware", "bootloader", "recovery", "ota", "adb",
        "fastboot", "recovery", "twrp", "magisk", "supersu", "busybox", "terminal",
        "shell", "bash", "zsh", "fish", "ssh", "ssl", "tls", "https", "http", "ftp",
        "smtp", "pop3", "imap", "dns", "dhcp", "nfs", "samba", "ldap", "kerberos",

        // 编程语言 (30)
        "java", "kotlin", "python", "ruby", "php", "swift", "rust", "go", "golang",
        "cplusplus", "csharp", "javascript", "typescript", "html", "css", "scss",
        "less", "sql", "plsql", "mongodb", "redis", "elasticsearch", "graphql",
        "rest", "soap", "xml", "json", "yaml", "toml", "protobuf",

        // 框架/库 (30)
        "spring", "hibernate", "mybatis", "struts", "react", "vue", "angular",
        "jquery", "bootstrap", "tailwind", "flutter", "reactnative", "xamarin",
        "cocos", "unity", "unreal", "godot", "opencv", "tensorflow", "pytorch",
        "keras", "scikit", "numpy", "pandas", "matplotlib", "django", "flask",
        "fastapi", "rails", "laravel",

        // 数据库 (20)
        "mysql", "postgresql", "oracle", "sqlite", "mongodb", "cassandra",
        "redis", "memcached", "elasticsearch", "solr", "clickhouse", "doris",
        "tidb", "oceanbase", "polardb", "gaussdb", "tair", "hbase", "hive", "spark",

        // 云/服务器 (25)
        "aws", "azure", "gcp", "aliyun", "tencentcloud", "baiducloud", "huaweicloud",
        "digitalocean", "linode", "vultr", "cloudflare", "fastly", "akamai",
        "nginx", "apache", "tomcat", "jetty", "undertow", "wildfly", "weblogic",
        "websphere", "jboss", "glassfish", "resin", "lighttpd",

        // 加密/安全 (20)
        "crypto", "aes", "rsa", "ecc", "sha256", "md5", "base64", "hex", "binary",
        "encrypt", "decrypt", "signature", "certificate", "firewall", "antivirus",
        "malware", "ransomware", "phishing", "spam", "hack",

        // 游戏/娱乐 (30)
        "game", "play", "music", "video", "movie", "tv", "show", "song", "album",
        "podcast", "stream", "broadcast", "live", "vod", "drama", "comedy",
        "action", "adventure", "rpg", "fps", "mmo", "moba", "card", "puzzle",
        "racing", "sports", "simulation", "strategy", "board", "arcade",

        // 科学/学术 (25)
        "physics", "chemistry", "biology", "math", "algebra", "calculus", "geometry",
        "statistics", "probability", "quantum", "relativity", "gravity", "magnetic",
        "electric", "thermal", "kinetic", "atomic", "molecular", "cellular", "genetic",
        "evolution", "ecosystem", "climate", "weather", "astronomy",

        // 医学/健康 (20)
        "medical", "health", "fitness", "nutrition", "vitamin", "protein", "carb",
        "fat", "sugar", "blood", "heart", "brain", "liver", "kidney", "lung",
        "bone", "muscle", "nerve", "cell", "dna",

        // 食物/饮品 (25)
        "coffee", "tea", "milk", "juice", "water", "soda", "beer", "wine", "whiskey",
        "vodka", "rum", "gin", "tequila", "brandy", "champagne", "pizza", "pasta",
        "burger", "sushi", "ramen", "taco", "burrito", "curry", "dimsum", "hotpot",

        // 自然/地理 (30)
        "ocean", "sea", "river", "lake", "mountain", "forest", "desert", "island",
        "volcano", "glacier", "canyon", "valley", "plateau", "plain", "hill",
        "beach", "reef", "delta", "fjord", "peninsula", "archipelago", "tundra",
        "taiga", "savanna", "jungle", "swamp", "wetland", "grassland", "meadow", "orchard",

        // 颜色 (20)
        "red", "blue", "green", "yellow", "orange", "purple", "pink", "brown",
        "black", "white", "gray", "gold", "silver", "bronze", "coral", "indigo",
        "violet", "magenta", "cyan", "teal",

        // 动物 (30)
        "lion", "tiger", "bear", "wolf", "fox", "deer", "rabbit", "mouse", "bird",
        "eagle", "hawk", "owl", "falcon", "shark", "whale", "dolphin", "seal",
        "otter", "beaver", "squirrel", "monkey", "gorilla", "chimpanzee", "panda",
        "koala", "kangaroo", "platypus", "armadillo", "anteater", "porcupine",

        // 城市 (30)
        "tokyo", "seoul", "beijing", "shanghai", "hongkong", "singapore", "bangkok",
        "mumbai", "dubai", "istanbul", "moscow", "london", "paris", "berlin",
        "rome", "madrid", "barcelona", "amsterdam", "brussels", "vienna", "zurich",
        "geneva", "copenhagen", "stockholm", "oslo", "helsinki", "reykjavik", "dublin",
        "edinburgh", "munich",

        // 更多通用词 (50)
        "system", "core", "lib", "data", "cache", "temp", "tmp", "log", "cfg", "conf",
        "config", "settings", "preferences", "profile", "user", "admin", "guest",
        "root", "home", "dev", "prod", "test", "stage", "beta", "alpha", "daily",
        "nightly", "release", "stable", "unstable", "legacy", "modern", "classic",
        "basic", "advanced", "pro", "max", "ultra", "premium", "deluxe", "lite",
        "nano", "micro", "mini", "mega", "giga", "tera", "peta", "exa", "zetta"
    };

    // ============ 后缀列表 (80+) ============
    private static final String[] EXTENSIONS = {
        // 系统/编程
        ".cache", ".dll", ".bin", ".dat", ".tmp", ".log", ".sys", ".core", 
        ".ota", ".idx", ".vdex", ".odex", ".jar", ".so", ".xml", ".json", 
        ".db", ".cfg", ".ini", ".yaml", ".toml", ".properties", ".conf",
        // 视频/音频
        ".wmv", ".mp4", ".avi", ".mkv", ".mov", ".flv", ".webm", ".m4v",
        ".mp3", ".wav", ".flac", ".aac", ".ogg", ".wma", ".m4a",
        // 图片
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg", ".tiff",
        // 文档
        ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt",
        ".md", ".rtf", ".odt", ".ods", ".odp",
        // 压缩
        ".zip", ".rar", ".7z", ".gz", ".tar", ".iso", ".img", ".xz",
        ".bz2", ".tgz", ".zst",
        // 移动端
        ".apk", ".aab", ".dex", ".class", ".java", ".kt", ".dart",
        // Web
        ".html", ".htm", ".css", ".scss", ".js", ".ts", ".jsx", ".tsx",
        ".php", ".jsp", ".asp", ".aspx",
        // 代码
        ".c", ".cpp", ".h", ".hpp", ".py", ".rb", ".go", ".rs", ".swift",
        ".pl", ".pm", ".sh", ".bash", ".zsh", ".fish"
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

        DocumentFile[] files = dir.listFiles();
        int count = 0;

        for (DocumentFile file : files) {
            if (file == null || !file.isFile()) {
                continue;
            }

            String oldName = file.getName();
            if (oldName == null) {
                continue;
            }

            String newName = generateRandomFileName();

            boolean success;
            try {
                success = file.renameTo(newName);
            } catch (Exception e) {
                success = false;
            }

            if (success) {
                appendLog(oldName, newName);
                count++;
            }
        }

        return count;
    }

    private String generateRandomFileName() {
        // 8种不同格式，让文件名更丰富
        int format = random.nextInt(8);
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        long ts = System.currentTimeMillis();
        
        switch (format) {
            case 0:
                // com.android.system.core.cache
                return randomWord() + "." + randomWord() + "." + 
                       randomWord() + "." + randomWord() + randomExtension();
            case 1:
                // proj_20260712_7k9d.dll
                return randomWord() + "_" + date + "_" + randomAlphanumeric(4) + randomExtension();
            case 2:
                // google_android_data_1765432100000.log
                return randomWord() + "_" + randomWord() + "_" + 
                       randomWord() + "_" + ts + randomExtension();
            case 3:
                // system-kernel-driver-7k9d.bin
                return randomWord() + "-" + randomWord() + "-" + 
                       randomWord() + "-" + randomAlphanumeric(4) + randomExtension();
            case 4:
                // 20260712_android_core_cache.dat
                return date + "_" + randomWord() + "_" + 
                       randomWord() + "_" + randomWord() + randomExtension();
            case 5:
                // android.system.core.7k9d.cache
                return randomWord() + "." + randomWord() + "." + 
                       randomWord() + "." + randomAlphanumeric(4) + randomExtension();
            case 6:
                // cache_1765432100000_7k9d.tmp
                return randomWord() + "_" + ts + "_" + randomAlphanumeric(4) + randomExtension();
            default:
                // android_1765432100000_cache.ota
                return randomWord() + "_" + ts + "_" + randomWord() + randomExtension();
        }
    }

    private String randomWord() {
        return WORDS[random.nextInt(WORDS.length)];
    }

    private String randomExtension() {
        return EXTENSIONS[random.nextInt(EXTENSIONS.length)];
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
        } catch (IOException ignored) {}
    }
}
