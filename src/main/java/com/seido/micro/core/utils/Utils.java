package com.seido.micro.core.utils;


/**
 * Class utils
 */
public class Utils{

    private Utils() {throw new IllegalStateException("Utility class");}

    /**
     * Method get format repository name must be in format owner/repo
     */
    public static String getFormatRepo(String owner,String repositoryName) {
        return owner.concat("/").concat(repositoryName);
    }
}
