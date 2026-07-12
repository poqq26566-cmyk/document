package com.example.renamer;

import java.io.File;
import java.security.SecureRandom;


public class FileRenamer {


    private final SecureRandom random =
            new SecureRandom();


    public int renameFiles(File dir){


        if(dir == null || !dir.exists()){
            return 0;
        }


        File[] files = dir.listFiles();


        if(files == null){
            return 0;
        }


        int count = 0;


        for(File file : files){


            if(file.isFile()){


                String newName =
                        "backup_"
                        + System.currentTimeMillis()
                        + "_"
                        + random.nextInt(9999)
                        + ".cache";


                File newFile =
                        new File(
                                file.getParent(),
                                newName
                        );


                if(file.renameTo(newFile)){

                    count++;

                }

            }

        }


        return count;

    }

}
