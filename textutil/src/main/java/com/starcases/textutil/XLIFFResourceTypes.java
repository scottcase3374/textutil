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

    XLIFFResourceTypes(String path, String file, String pathTemplate)
    {
        this.path = path;
        this.file = file;
        this.pathTemplate = pathTemplate;
    }

    /**
     * luf
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
            try (XLIFFFilter reader = new XLIFFFilter();)
            {
                reader.open(rd, true);
                // Loop through the reader events
                while (reader.hasNext())
                {
                    net.sf.okapi.common.Event event = reader.next();

                    // Do something: here print the source content
                    if (event.isTextUnit() && event.getTextUnit().getId().equals(getFile() + "." + key))
                    {
                        ITextUnit unit = event.getTextUnit();

                        log.info("unit id: " + unit.getId());

                        net.sf.okapi.common.resource.Segment s = unit.getTargetSegment(tgtlocaleid, "0", false);
                        log.info("tgt unit seg text: " + s.text.getText());
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
    private String path;
    private String file;
    private String pathTemplate;

    private static Logger log = Logger.getLogger(TextUtil.class.getName());
}
