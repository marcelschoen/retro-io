package com.retroio.tools;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Some unit tests.
 *
 * @author Marcel Schoen
 */
public class ImageTypeTest {

    @Test
    public void testGettingType() {
        ImageType type;

        type = ImageType.getTypeFromFileSuffix(".ST");
        assertThat(type, is(ImageType.atarist_ST));

        type = ImageType.getTypeFromFileSuffix("mSa");
        assertThat(type, is(ImageType.atarist_MSA));

        type = ImageType.getTypeFromFileSuffix("aDf");
        assertThat(type, is(ImageType.amiga_ADF));

        type = ImageType.getTypeFromFileSuffix(".D64");
        assertThat(type, is(ImageType.c64_D64));

        type = ImageType.getTypeFromFileSuffix("img");
        assertThat(type, is(ImageType.dos_IMG));

        type = ImageType.getTypeFromFileSuffix(".img");
        assertThat(type, is(ImageType.dos_IMG));

        type = ImageType.getTypeFromFileSuffix("xyz");
        assertThat(type, is(ImageType.unknown));
    }
}
