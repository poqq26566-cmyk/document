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

    // ============ 4000+ 超级词库（无城市/国家） ============
    private static final String[] WORDS = {
        // === 公司/品牌 (100) ===
        "google", "microsoft", "apple", "amazon", "facebook", "twitter", "instagram",
        "whatsapp", "telegram", "signal", "zoom", "slack", "discord", "reddit",
        "netflix", "spotify", "uber", "airbnb", "dropbox", "box", "salesforce",
        "oracle", "ibm", "hp", "dell", "cisco", "vmware", "sap", "adobe", "autodesk",
        "nokia", "ericsson", "motorola", "lg", "sony", "panasonic", "philips",
        "samsung", "xiaomi", "huawei", "oppo", "vivo", "zte", "lenovo", "asus",
        "acer", "gigabyte", "msi", "coolpad", "oneplus", "blackberry", "htc",
        "sharp", "fujitsu", "nec", "olympus", "canon", "nikon", "fujifilm",
        "leica", "zeiss", "rolex", "omega", "tagheuer", "breitling", "panerai",
        "iwc", "cartier", "tiffany", "gucci", "prada", "chanel", "hermes",
        "louisvuitton", "dior", "versace", "armani", "burberry", "coach", "nike",
        "adidas", "puma", "reebok", "underarmour", "newbalance", "asics", "converse",
        "vans", "timberland", "columbia", "patagonia", "tnf", "arcteryx", "mammut",
        "northface", "champion", "offwhite", "supreme", "palace", "carhartt", "nb",

        // === 中国公司 (80) ===
        "alibaba", "tencent", "baidu", "huawei", "xiaomi", "oppo", "vivo", "meizu",
        "oneplus", "zte", "lenovo", "asus", "acer", "gigabyte", "msi", "coolpad",
        "gionee", "hisense", "tcl", "skyworth", "konka", "changhong", "haier",
        "gree", "midea", "galanz", "supor", "joyoung", "roborock", "ecovacs",
        "dji", "xiaopeng", "nio", "li", "byton", "geely", "chery", "byd",
        "greatwall", "changan", "dongfeng", "sail", "fpv", "hummer", "tan",
        "weilai", "tesla", "nvidia", "amd", "intel", "qualcomm", "mediatek",
        "rockchip", "amlogic", "allwinner", "sprd", "smsc", "realtek", "broadcom",
        "marvell", "freescale", "nxp", "stm", "ti", "adi", "maxim", "infineon",
        "onsemi", "diodes", "lattice", "xilinx", "altera", "microchip", "atmel",
        "cypress", "silabs", "esp", "raspberry", "arduino", "beaglebone", "bananapi",
        "orangepi", "rockpi", "nanopi", "pine64", "olimex", "adafruit", "sparkfun",

        // === 系统/技术 (80) ===
        "android", "ios", "windows", "linux", "unix", "macos", "tvos", "watchos",
        "kernel", "driver", "firmware", "bootloader", "recovery", "ota", "adb",
        "fastboot", "twrp", "magisk", "supersu", "busybox", "terminal",
        "shell", "bash", "zsh", "fish", "ssh", "ssl", "tls", "https", "http", "ftp",
        "smtp", "pop3", "imap", "dns", "dhcp", "nfs", "samba", "ldap", "kerberos",
        "nfc", "bluetooth", "wifi", "5g", "4g", "lte", "gps", "glonass", "galileo",
        "beidou", "zigbee", "thread", "matter", "homekit", "alexa", "assistant",
        "siri", "bixby", "cortana", "heygoogle", "okgoogle", "heyxiaomi", "heysiri",
        "migration", "archive", "cloud", "sync", "backup", "restore", "update",
        "upgrade", "patch", "hotfix", "rollback", "flash", "burn", "brick", "unbrick",

        // === 编程语言 (50) ===
        "java", "kotlin", "python", "ruby", "php", "swift", "rust", "go", "golang",
        "cplusplus", "csharp", "javascript", "typescript", "html", "css", "scss",
        "less", "sql", "plsql", "mongodb", "redis", "elasticsearch", "graphql",
        "rest", "soap", "xml", "json", "yaml", "toml", "protobuf",
        "dart", "flutter", "react", "vue", "angular", "svelte", "solidjs",
        "qwik", "alpine", "stimulus", "htmx", "wasm", "assembly", "fortran",
        "pascal", "ada", "lisp", "scheme", "clojure", "elixir", "erlang",

        // === 框架/库 (50) ===
        "spring", "hibernate", "mybatis", "struts", "react", "vue", "angular",
        "jquery", "bootstrap", "tailwind", "flutter", "reactnative", "xamarin",
        "cocos", "unity", "unreal", "godot", "opencv", "tensorflow", "pytorch",
        "keras", "scikit", "numpy", "pandas", "matplotlib", "django", "flask",
        "fastapi", "rails", "laravel", "symfony", "codeigniter", "cakephp",
        "phoenix", "elixir", "clojure", "groovy", "scala", "haskell", "erlang",
        "nodejs", "deno", "bun", "express", "nestjs", "nextjs", "nuxtjs",
        "gatsby", "next", "remix", "swiftui", "combine", "rxjava", "coroutines",

        // === 数据库 (40) ===
        "mysql", "postgresql", "oracle", "sqlite", "mongodb", "cassandra",
        "redis", "memcached", "elasticsearch", "solr", "clickhouse", "doris",
        "tidb", "oceanbase", "polardb", "gaussdb", "tair", "hbase", "hive", "spark",
        "flink", "kafka", "pulsar", "rabbitmq", "rocketmq", "activemq", "zeromq",
        "influxdb", "prometheus", "grafana", "victoriametrics", "thanos", "cortex",
        "loki", "tempo", "phlare", "mimir", "alertmanager", "zookeeper", "etcd", "consul",

        // === 云/服务器 (40) ===
        "aws", "azure", "gcp", "aliyun", "tencentcloud", "baiducloud", "huaweicloud",
        "digitalocean", "linode", "vultr", "cloudflare", "fastly", "akamai",
        "nginx", "apache", "tomcat", "jetty", "undertow", "wildfly", "weblogic",
        "websphere", "jboss", "glassfish", "resin", "lighttpd", "caddy", "traefik",
        "envoy", "haproxy", "varnish", "squid", "dnsmasq", "bind", "unbound", "powerdns",
        "minio", "ceph", "glusterfs", "longhorn", "rancher", "k3s", "k8s", "openshift",

        // === 加密/安全 (40) ===
        "crypto", "aes", "rsa", "ecc", "sha256", "md5", "base64", "hex", "binary",
        "encrypt", "decrypt", "signature", "certificate", "firewall", "antivirus",
        "malware", "ransomware", "phishing", "spam", "hack", "exploit", "vulnerability",
        "patch", "update", "hotfix", "backdoor", "trojan", "worm", "virus", "rootkit",
        "keylogger", "adware", "spyware", "scareware", "cryptojacking", "blockchain",
        "wallet", "mining", "hashrate", "difficulty", "nonce", "merkle", "oracle",

        // === 游戏/娱乐 (60) ===
        "game", "play", "music", "video", "movie", "tv", "show", "song", "album",
        "podcast", "stream", "broadcast", "live", "vod", "drama", "comedy",
        "action", "adventure", "rpg", "fps", "mmo", "moba", "card", "puzzle",
        "racing", "sports", "simulation", "strategy", "board", "arcade",
        "minecraft", "fortnite", "valorant", "csgo", "dota", "lol", "wow",
        "overwatch", "diablo", "starcraft", "warcraft", "hearthstone", "pubg",
        "cod", "bf", "apex", "warzone", "destiny", "halo", "gears", "uncharted",
        "godofwar", "horizon", "zelda", "mario", "pokemon", "sonic", "pacman",
        "tetris", "snake", "minesweeper", "solitaire", "chess", "go", "mahjong",

        // === 科学/学术 (60) ===
        "physics", "chemistry", "biology", "math", "algebra", "calculus", "geometry",
        "statistics", "probability", "quantum", "relativity", "gravity", "magnetic",
        "electric", "thermal", "kinetic", "atomic", "molecular", "cellular", "genetic",
        "evolution", "ecosystem", "climate", "weather", "astronomy", "astrophysics",
        "cosmology", "geology", "oceanography", "meteorology", "hydrology", "ecology",
        "botany", "zoology", "microbiology", "biochemistry", "neurobiology", "immunology",
        "pathology", "pharmacology", "epidemiology", "toxicology", "embryology",
        "biophysics", "geophysics", "thermodynamics", "mechanics", "optics", "acoustics",
        "nuclear", "particle", "stringtheory", "blackhole", "nebula", "galaxy", "quasar",
        "pulsar", "supernova", "redgiant", "whitedwarf", "neutronstar", "exoplanet",

        // === 医学/健康 (50) ===
        "medical", "health", "fitness", "nutrition", "vitamin", "protein", "carb",
        "fat", "sugar", "blood", "heart", "brain", "liver", "kidney", "lung",
        "bone", "muscle", "nerve", "cell", "dna", "rna", "enzyme", "hormone",
        "antibody", "vaccine", "diagnosis", "therapy", "surgery", "cardiology",
        "neurology", "orthopedics", "dermatology", "ophthalmology", "dentistry", "psychiatry",
        "pathology", "radiology", "oncology", "gynecology", "pediatrics", "geriatrics",
        "urology", "nephrology", "gastroenterology", "hepatology", "rheumatology",
        "endocrinology", "hematology", "allergy", "immunotherapy", "chemotherapy",

        // === 食物/饮品 (80) ===
        "coffee", "tea", "milk", "juice", "water", "soda", "beer", "wine", "whiskey",
        "vodka", "rum", "gin", "tequila", "brandy", "champagne", "pizza", "pasta",
        "burger", "sushi", "ramen", "taco", "burrito", "curry", "dimsum", "hotpot",
        "steak", "salmon", "tuna", "lobster", "crab", "shrimp", "oyster", "clam",
        "mussel", "scallop", "tofu", "tempeh", "seitan", "quinoa", "kale", "spinach",
        "broccoli", "avocado", "mango", "pineapple", "coconut", "durian", "lychee",
        "longan", "rambutan", "papaya", "guava", "passionfruit", "dragonfruit",
        "jackfruit", "breadfruit", "starfruit", "custardapple", "soursop",
        "banana", "orange", "apple", "pear", "peach", "plum", "cherry", "grape",
        "watermelon", "cantaloupe", "honeydew", "kiwi", "fig", "date", "olive",
        "almond", "walnut", "cashew", "pistachio", "pecan", "macadamia", "hazelnut",

        // === 自然/地理 (60) ===
        "ocean", "sea", "river", "lake", "mountain", "forest", "desert", "island",
        "volcano", "glacier", "canyon", "valley", "plateau", "plain", "hill",
        "beach", "reef", "delta", "fjord", "peninsula", "archipelago", "tundra",
        "taiga", "savanna", "jungle", "swamp", "wetland", "grassland", "meadow", "orchard",
        "waterfall", "geyser", "cave", "cliff", "mesa", "butte", "prairie", "steppe",
        "rainforest", "mangrove", "estuary", "bay", "gulf", "strait", "channel", "sound",
        "harbor", "port", "dock", "pier", "lagoon", "atoll", "reef", "bank", "shoal",
        "ridge", "plateau", "basin", "dune", "oasis", "wadi", "sinkhole", "grotto",

        // === 颜色 (50) ===
        "red", "blue", "green", "yellow", "orange", "purple", "pink", "brown",
        "black", "white", "gray", "gold", "silver", "bronze", "coral", "indigo",
        "violet", "magenta", "cyan", "teal", "amber", "crimson", "emerald", "jade",
        "lavender", "mauve", "ochre", "plum", "ruby", "sapphire", "amethyst",
        "aquamarine", "beryl", "citrine", "garnet", "jade", "malachite", "obsidian",
        "onyx", "opal", "pearl", "peridot", "rose", "ruby", "sapphire", "topaz",
        "turquoise", "zircon", "cobalt", "scarlet", "vermilion", "cerulean", "azure",

        // === 动物 (100) ===
        "lion", "tiger", "bear", "wolf", "fox", "deer", "rabbit", "mouse", "bird",
        "eagle", "hawk", "owl", "falcon", "shark", "whale", "dolphin", "seal",
        "otter", "beaver", "squirrel", "monkey", "gorilla", "chimpanzee", "panda",
        "koala", "kangaroo", "platypus", "armadillo", "anteater", "porcupine",
        "elephant", "rhino", "hippo", "giraffe", "zebra", "buffalo", "bison",
        "moose", "elk", "caribou", "antelope", "gazelle", "cheetah", "leopard",
        "panther", "jaguar", "cougar", "lynx", "bobcat", "coyote", "hyena",
        "jackal", "meerkat", "marmot", "chipmunk", "hedgehog", "mole", "shrew",
        "bat", "sloth", "anteater", "armadillo", "camel", "llama", "alpaca",
        "vicuna", "guanaco", "donkey", "mule", "horse", "pony", "zebra",
        "pig", "boar", "warthog", "hippo", "rhino", "elephant", "mammoth",
        "sabertooth", "woolly", "mastodon", "reindeer", "caribou", "moose",
        "elk", "deer", "antelope", "gazelle", "springbok", "impala", "kudu",
        "eland", "oryx", "addax", "nubian", "ibex", "chamois", "muskox",

        // === 植物/花卉 (60) ===
        "rose", "tulip", "lily", "orchid", "daisy", "sunflower", "lavender",
        "jasmine", "gardenia", "peony", "chrysanthemum", "daffodil", "iris",
        "lilac", "magnolia", "hydrangea", "azalea", "camellia", "geranium",
        "petunia", "marigold", "zinnia", "cosmos", "pansy", "violet", "poppy",
        "buttercup", "bluebell", "foxglove", "hollyhock", "delphinium", "lupine",
        "snowdrop", "crocus", "hyacinth", "freesia", "gladiolus", "begonia",
        "fern", "moss", "lichen", "algae", "seaweed", "kelp", "coral",
        "bamboo", "cedar", "pine", "oak", "maple", "birch", "willow",
        "ash", "elm", "beech", "walnut", "chestnut", "hickory", "sycamore",

        // === 音乐/乐器 (40) ===
        "piano", "guitar", "violin", "cello", "bass", "drums", "flute", "oboe",
        "clarinet", "saxophone", "trumpet", "trombone", "tuba", "harp", "organ",
        "accordion", "harmonica", "banjo", "mandolin", "ukulele", "sitar", "tabla",
        "djembe", "bongo", "marimba", "xylophone", "vibraphone", "triangle",
        "cymbal", "gong", "chimes", "bell", "whistle", "bagpipe", "didgeridoo",
        "shamisen", "koto", "erhu", "pipa", "guzheng",

        // === 运动 (50) ===
        "soccer", "basketball", "football", "baseball", "tennis", "golf", "swimming",
        "running", "jumping", "boxing", "wrestling", "judo", "karate", "taekwondo",
        "kungfu", "yoga", "pilates", "cycling", "skating", "snowboarding", "skiing",
        "surfing", "sailing", "rowing", "kayaking", "rafting", "climbing", "hiking",
        "trekking", "camping", "fishing", "hunting", "archery", "shooting", "fencing",
        "gymnastics", "diving", "waterpolo", "volleyball", "handball", "rugby",
        "cricket", "badminton", "squash", "racquetball", "pickleball", "padel",
        "bowling", "darts", "snooker", "pool", "billiards",

        // === 宇宙/天文 (40) ===
        "sun", "moon", "star", "planet", "comet", "asteroid", "meteor", "galaxy",
        "nebula", "quasar", "pulsar", "blackhole", "supernova", "redgiant", "whitedwarf",
        "neutronstar", "exoplanet", "milkyway", "andromeda", "orion", "sirius",
        "polaris", "vega", "altair", "betelgeuse", "rigel", "antares", "canopus",
        "achernar", "procyon", "acamar", "formaldehyde", "glycolaldehyde",
        "helium", "hydrogen", "lithium", "beryllium", "carbon", "nitrogen",
        "oxygen", "neon", "argon", "krypton", "xenon", "radon",

        // === 神话/传说 (40) ===
        "zeus", "athena", "apollo", "artemis", "ares", "hermes", "poseidon",
        "hades", "aphrodite", "hephaestus", "demeter", "hestia", "dionysus",
        "odin", "thor", "loki", "freya", "heimdall", "balder", "tyr",
        "ra", "anubis", "isis", "osiris", "horus", "set", "bastet",
        "goku", "vegeta", "naruto", "sasuke", "luffy", "zoro", "nami",
        "ichigo", "renji", "asta", "yuno", "midoriya", "bakugo", "todoroki",

        // === 通用词 (200) ===
        "system", "core", "lib", "data", "cache", "temp", "tmp", "log", "cfg", "conf",
        "config", "settings", "preferences", "profile", "user", "admin", "guest",
        "root", "home", "dev", "prod", "test", "stage", "beta", "alpha", "daily",
        "nightly", "release", "stable", "unstable", "legacy", "modern", "classic",
        "basic", "advanced", "pro", "max", "ultra", "premium", "deluxe", "lite",
        "nano", "micro", "mini", "mega", "giga", "tera", "peta", "exa", "zetta",
        "yotta", "bronto", "geop", "sagan", "gupta", "centillion", "googol", "googolplex",
        "infinity", "eternity", "forever", "never", "always", "never", "ever", "soon",
        "later", "now", "then", "once", "twice", "thrice", "final", "initial", "first",
        "last", "next", "prev", "current", "default", "custom", "new", "old",
        "active", "inactive", "pending", "complete", "running", "stopped", "ready",
        "unknown", "known", "visible", "hidden", "normal", "emergency", "critical",
        "info", "debug", "trace", "error", "fatal", "warn", "success", "fail",
        "start", "stop", "pause", "resume", "retry", "skip", "abort", "exit",
        "enter", "login", "logout", "register", "verify", "validate", "authenticate",
        "authorize", "encrypt", "decrypt", "compress", "decompress", "encode", "decode",
        "read", "write", "append", "delete", "copy", "move", "rename", "symlink",
        "hardlink", "mount", "umount", "sync", "async", "block", "nonblock",
        "socket", "pipe", "fifo", "unix", "domain", "stream", "datagram", "seqpacket",
        "raw", "cooked", "binary", "ascii", "utf8", "utf16", "utf32", "hex", "octal",
        "decimal", "float", "double", "long", "short", "byte", "bit", "nibble", "word"
    };

    // ============ 后缀列表 (150+) ============
    private static final String[] EXTENSIONS = {
        // 系统/编程 (35)
        ".cache", ".dll", ".bin", ".dat", ".tmp", ".log", ".sys", ".core", 
        ".ota", ".idx", ".vdex", ".odex", ".jar", ".so", ".xml", ".json", 
        ".db", ".cfg", ".ini", ".yaml", ".toml", ".properties", ".conf",
        ".lock", ".pid", ".socket", ".pipe", ".fifo", ".dev", ".null",
        ".ko", ".o", ".a", ".la", ".diag", ".manifest",
        // 视频/音频 (35)
        ".wmv", ".mp4", ".avi", ".mkv", ".mov", ".flv", ".webm", ".m4v",
        ".mpg", ".mpeg", ".3gp", ".mts", ".m2ts", ".ts", ".vob", ".ogv",
        ".mp3", ".wav", ".flac", ".aac", ".ogg", ".wma", ".m4a", ".opus", ".ac3",
        ".dts", ".ec3", ".aiff", ".alac", ".amr", ".awb", ".mmf", ".imy",
        ".xmf", ".mxmf", ".rtttl", ".ota",
        // 图片 (30)
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg", ".tiff",
        ".tif", ".heic", ".heif", ".avif", ".raw", ".cr2", ".nef", ".arw",
        ".dng", ".orf", ".rw2", ".jfif", ".exr", ".hdr", ".pbm", ".pgm",
        ".ppm", ".xbm", ".xpm", ".ico", ".cur", ".ani",
        // 文档 (35)
        ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt",
        ".md", ".rtf", ".odt", ".ods", ".odp", ".odg", ".odf", ".csv",
        ".tsv", ".psd", ".ai", ".eps", ".cdr", ".xps", ".pages", ".numbers", ".key",
        ".tex", ".latex", ".bib", ".sty", ".cls", ".dtx", ".ins", ".idx",
        ".ilg", ".ind", ".ist",
        // 压缩 (35)
        ".zip", ".rar", ".7z", ".gz", ".tar", ".iso", ".img", ".xz",
        ".bz2", ".tgz", ".zst", ".lz", ".lz4", ".lzma", ".zst", ".br",
        ".arj", ".cab", ".dmg", ".hqx", ".sit", ".sea", ".bin", ".cue", ".mdf",
        ".mds", ".nrg", ".vcd", ".cdi", ".bwi", ".bwt", ".ccd", ".sub",
        ".img", ".udf", ".hfs",
        // 移动端 (20)
        ".apk", ".aab", ".dex", ".class", ".java", ".kt", ".dart", ".dex",
        ".odex", ".vdex", ".oat", ".art", ".scm", ".diff", ".patch",
        ".xapk", ".apks", ".apkm", ".xapk", ".obb",
        // Web (25)
        ".html", ".htm", ".css", ".scss", ".js", ".ts", ".jsx", ".tsx",
        ".php", ".jsp", ".asp", ".aspx", ".wasm", ".webmanifest", ".wpt",
        ".vue", ".svelte", ".astro", ".liquid", ".hbs", ".ejs", ".pug",
        ".haml", ".slim", ".erbl",
        // 代码 (25)
        ".c", ".cpp", ".h", ".hpp", ".py", ".rb", ".go", ".rs", ".swift",
        ".pl", ".pm", ".sh", ".bash", ".zsh", ".fish", ".lua", ".r", ".m",
        ".mm", ".vim", ".el", ".erl", ".hrl", ".ex", ".exs",
        // 更多 (15)
        ".torrent", ".part", ".crdownload", ".download", ".resume",
        ".bak", ".old", ".new", ".tmp", ".temp", ".sav", ".save",
        ".state", ".checkpoint", ".snapshot"
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
        // 20种不同格式，让文件名极其丰富
        int format = random.nextInt(20);
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        long ts = System.currentTimeMillis();
        String id1 = randomAlphanumeric(random.nextInt(4) + 3);
        String id2 = randomAlphanumeric(random.nextInt(4) + 3);
        String id3 = randomAlphanumeric(random.nextInt(4) + 3);
        
        switch (format) {
            case 0:
                return randomWord() + "." + randomWord() + "." + 
                       randomWord() + "." + randomWord() + randomExtension();
            case 1:
                return randomWord() + "_" + date + "_" + id1 + randomExtension();
            case 2:
                return randomWord() + "_" + randomWord() + "_" + 
                       randomWord() + "_" + ts + randomExtension();
            case 3:
                return randomWord() + "-" + randomWord() + "-" + 
                       randomWord() + "-" + id1 + randomExtension();
            case 4:
                return date + "_" + randomWord() + "_" + 
                       randomWord() + "_" + randomWord() + randomExtension();
            case 5:
                return randomWord() + "." + randomWord() + "." + 
                       randomWord() + "." + id1 + randomExtension();
            case 6:
                return randomWord() + "_" + ts + "_" + id1 + randomExtension();
            case 7:
                return randomWord() + "_" + ts + "_" + randomWord() + randomExtension();
            case 8:
                return randomWord() + "-" + ts + "-" + randomWord() + randomExtension();
            case 9:
                return id1 + "_" + randomWord() + "_" + randomWord() + 
                       "_" + date + randomExtension();
            case 10:
                return randomWord() + "." + randomWord() + "." + 
                       date + "." + id1 + randomExtension();
            case 11:
                return randomWord() + "_" + randomWord() + "_" + 
                       id1 + "_" + ts + randomExtension();
            case 12:
                return randomWord() + "." + id1 + "." + randomWord() + 
                       "." + id2 + randomExtension();
            case 13:
                return date + "-" + id1 + "-" + randomWord() + 
                       "-" + randomWord() + randomExtension();
            case 14:
                return randomWord() + "_" + randomWord() + "_" + 
                       randomWord() + "_" + randomWord() + randomExtension();
            case 15:
                return id1 + "-" + id2 + "-" + date + "-" + 
                       randomWord() + randomExtension();
            case 16:
                return randomWord() + "." + randomWord() + "." + 
                       randomWord() + "." + randomWord() + "." + id1 + randomExtension();
            case 17:
                return randomWord() + "_" + date + "_" + randomWord() + 
                       "_" + ts + randomExtension();
            case 18:
                return date + "_" + id1 + "_" + randomWord() + 
                       "_" + id2 + randomExtension();
            default:
                return randomWord() + "-" + randomWord() + "-" + 
                       date + "-" + id1 + "-" + ts + randomExtension();
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
