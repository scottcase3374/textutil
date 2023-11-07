package com.starcases.textutil;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.xliff.XLIFFFilter;

public enum XLIFFResourceTypes implements Resource
{
    COUNTRIES("countries", "country", "/data/%s/%s/country.xliff");

    XLIFFResourceTypes(final String path, final String file, final String pathTemplate)
    {
        this.path = path;
        this.file = file;
        this.pathTemplate = pathTemplate;
    }

    /**
     *
     * @param key
     * @param tgtlocaleid

     * @return
     * @throws FileNotFoundException
     */
    @Override
    public Optional<String> getVal(final String key, final LocaleId tgtlocaleid) throws FileNotFoundException
    {
        String retVal = null;
        try (RawDocument rd = new RawDocument(
                TextUtil.class.getResourceAsStream(firstValidLocalePath(tgtlocaleid, key).orElse("EN")), "UTF-8", LocaleId.US_ENGLISH, tgtlocaleid);
                )
        {
            try (final XLIFFFilter reader = new XLIFFFilter();)
            {
                reader.open(rd, true);
                // Loop through the reader events
                while (reader.hasNext())
                {
                    net.sf.okapi.common.Event event = reader.next();

                    // Do something: here print the source content
                    if (event.isTextUnit() && event.getTextUnit().getId().equals(getFile() + "." + key))
                    {
                        final ITextUnit unit = event.getTextUnit();
                        net.sf.okapi.common.resource.Segment s = unit.getTargetSegment(tgtlocaleid, "0", false);
                        retVal = s.text.getText();
                    }
                }
            }
        }
        return Optional.ofNullable(retVal);
    }

    private Optional<String> firstValidLocalePath(final LocaleId tgtlocaleid, final String key)
    {
        String pathResult = null;
        if (TextUtil.class.getResource(Path.of(String.format(getPathTemplate(), getPath(), tgtlocaleid.toString() + "_" + getFile() )).toString()) != null)
        {
            pathResult = String.format(pathTemplate, getPath(), tgtlocaleid.toString() + "_" + key );
        }
        else if (TextUtil.class.getResource(Path.of(String.format(getPathTemplate(), getPath(), tgtlocaleid.toString())).toString()) != null)
        {
            pathResult = String.format(getPathTemplate(), getPath(), tgtlocaleid.toString());
        }
        if (log.isLoggable(Level.INFO))
        {
            log.info(String.format(" locale %s  %s %s path : %s ", tgtlocaleid.toString(), name(), key, null != path ? path : "<null>"));
        }
        return Optional.ofNullable(pathResult);
    }

    String getPath()
    {
        return path;
    }

    String getFile()
    {
        return file;
    }

    String getPathTemplate()
    {
        return pathTemplate;
    }

    private final String path;
    private final String file;
    private final String pathTemplate;

    private static final Logger log = Logger.getLogger(TextUtil.class.getName());
}
