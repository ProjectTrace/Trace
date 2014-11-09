package com.uw.hcde.fizzlab.trace.model.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Parse data model for a drawing object.
 * <p/>
 * Scheme:
 * px_x_list(number list), px_y_list(number list), creator(user id),
 * receiver_list(list of user id), annotation_list(list of annotation id)
 * <p/>
 * Constrains:
 * px_x_list and px_y_list must be of equal length
 * creator is not empty
 *
 * @author tianchi
 */

@ParseClassName("Drawing")
public class ParseDrawing extends ParseObject {

    // Related table keys
    private static final String KEY_PX_X_LIST = "px_x_list";
    private static final String KEY_PX_Y_LIST = "px_y_list";
    private static final String KEY_CREATOR = "creator";
    private static final String KEY_RECEIVER_LIST = "receiver_list";
    private static final String KEY_ANNOTATION_LIST = "annotation_list";

    /**
     * Sets x list
     *
     * @param xList
     */
    public void setXList(List<Integer> xList) {
        addAll(KEY_PX_X_LIST, xList);
    }

    /**
     * Gets x list
     *
     * @return list
     */
    public List<Integer> getXList() {
        return getList(KEY_PX_X_LIST);
    }

    /**
     * Sets y list
     *
     * @param yList
     */
    public void setYList(List<Integer> yList) {
        addAll(KEY_PX_Y_LIST, yList);
    }

    /**
     * Gets y list
     *
     * @return list
     */
    public List<Integer> getYList() {
        return getList(KEY_PX_Y_LIST);
    }

    /**
     * Sets creator id
     *
     * @param creatorID
     */
    public void setCreator(String creatorID) {
        put(KEY_CREATOR, creatorID);
    }

    /**
     * Gets creator id
     *
     * @return
     */
    public String getCreator() {
        return getString(KEY_CREATOR);
    }

    /**
     * Sets receiver id list
     *
     * @param receiverIDs
     */
    public void setReceiverList(List<String> receiverIDs) {
        addAll(KEY_RECEIVER_LIST, receiverIDs);
    }

    /**
     * Gets receiver id list
     *
     * @return
     */
    public List<String> getReceiverList() {
        return getList(KEY_RECEIVER_LIST);
    }

    /**
     * Sets annotation id list
     *
     * @param annotationIDs
     */
    public void setAnnotationList(List<String> annotationIDs) {
        addAll(KEY_ANNOTATION_LIST, annotationIDs);
    }

    /**
     * Gets annotation id list
     *
     * @return
     */
    public List<String> getAnnotationList() {
        return getList(KEY_ANNOTATION_LIST);
    }

    /**
     * Gets query
     *
     * @return query
     */
    public static ParseQuery<ParseDrawing> getQuery() {
        return ParseQuery.getQuery(ParseDrawing.class);
    }
}
