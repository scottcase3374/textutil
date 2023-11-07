package com.starcases.textutil;

import java.io.FileNotFoundException;
import java.util.Optional;

import net.sf.okapi.common.LocaleId;

public interface Resource
{
    Optional<String> getVal(final String key, final LocaleId tgtlocaleid) throws FileNotFoundException;
}
