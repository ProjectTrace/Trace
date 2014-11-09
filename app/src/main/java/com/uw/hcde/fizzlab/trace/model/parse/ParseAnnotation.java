package com.uw.hcde.fizzlab.trace.model.parse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Parse data model for an annotation object.
 * <p/>
 * Scheme:
 * px_x(number), px_y(number), text(String), image(file), audio(file), video(file)
 * <p/>
 * Constrains:
 * px_x and px_y are not empty
 * At least one of the annotation item is not empty
 *
 * @author tianchi
 */

@ParseClassName("Annotation")
public class ParseAnnotation extends ParseObject {

    // Related table keys
    private static final String KEY_PX_X = "px_x";
    private static final String KEY_PX_Y = "px_y";

    private static final String KEY_TEXT = "text";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_VIDEO = "video";
    private static final String KEY_AUDIO = "audio";

    /**
     * Sets px x
     *
     * @param x
     */
    public void setX(int x) {
        put(KEY_PX_X, x);
    }

    /**
     * Gets px x
     *
     * @return
     */
    public int getX() {
        return getInt(KEY_PX_X);
    }

    /**
     * Sets px y
     *
     * @param y
     */
    public void setY(int y) {
        put(KEY_PX_Y, y);
    }

    /**
     * Gets px y
     *
     * @return
     */
    public int getY() {
        return getInt(KEY_PX_Y);
    }


    /**
     * Sets text
     *
     * @param value
     */
    public void setText(String value) {
        put(KEY_TEXT, value);
    }

    /**
     * Gets text
     *
     * @return text
     */
    public String getText() {
        return getString(KEY_TEXT);
    }


    /**
     * Sets image
     *
     * @param value
     */
    public void setImage(ParseFile value) {
        put(KEY_IMAGE, value);
    }

    /**
     * Gets image
     *
     * @return image
     */
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    /**
     * Sets audio
     *
     * @param value
     */
    public void setAudio(ParseFile value) {
        put(KEY_AUDIO, value);
    }

    /**
     * Gets audio
     *
     * @return
     */
    public ParseFile getAudio() {
        return getParseFile(KEY_AUDIO);
    }

    /**
     * Sets video
     *
     * @param value
     */
    public void setVideo(ParseFile value) {
        put(KEY_VIDEO, value);
    }

    /**
     * Gets video
     *
     * @return
     */
    public ParseFile getVideo() {
        return getParseFile(KEY_VIDEO);
    }

    /**
     * Gets query
     *
     * @return query
     */
    public static ParseQuery<ParseAnnotation> getQuery() {
        return ParseQuery.getQuery(ParseAnnotation.class);
    }
}
