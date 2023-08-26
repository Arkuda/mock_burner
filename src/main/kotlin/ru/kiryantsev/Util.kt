package ru.kiryantsev

import java.io.File

class Util {
    companion object {

        val currentWorkingDir: String = System.getProperty("user.dir")

        fun createDirs(path: String){
            File(path).apply {
                if (!this.exists()){
                    this.mkdirs();
                }
            }
        }
    }
}