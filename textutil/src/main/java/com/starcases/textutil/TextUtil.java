package com.starcases.textutil;

import java.io.FileNotFoundException;
import java.util.logging.Logger;
import net.sf.okapi.common.LocaleId;

public class TextUtil
{
    Logger log = Logger.getLogger(TextUtil.class.getName());

    public static void main(String[] args) throws FileNotFoundException
    {
        var tgtlocaleid = LocaleId.ENGLISH;
        var key = "US";
        XLIFFResourceTypes.COUNTRIES.getVal(key, tgtlocaleid);
    }
}
