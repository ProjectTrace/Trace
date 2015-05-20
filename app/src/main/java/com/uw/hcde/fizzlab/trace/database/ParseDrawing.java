package com.uw.hcde.fizzlab.trace.database;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Parse data model for a drawing object.
 * <p/>
 * Scheme:
 * px_x_list(number list), px_y_list(number list), creator(user id),
 * receiver_list(list of user id), annotation_list(list of annotation id), description(string)
 * <p/>
 * Constrains:
 * px_x_list and px_y_list must be of equal length
 * creator is not empty
 *
 * @author tianchi
 */

@ParseClassName("Drawing")
public class ParseDrawing extends ParseObject implements Comparable<ParseDrawing> {

    // Related table keys
    public static final String KEY_PX_X_LIST = "px_x_list";
    public static final String KEY_PX_Y_LIST = "px_y_list";

    // Receiver_Record and to Createtor_Record for record to keep track of drawing after deletion from one side
    public static final String KEY_CREATOR_RECORD = "creator_record";
    public static final String KEY_RECEIVER_RECORD = "receiver_record";
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_RECEIVER_LIST = "receiver_list";
    public static final String KEY_WALKED_USERS_LIST = "walked_users_list";


    public static final String KEY_ANNOTATION_LIST = "annotation_list";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TAG = "tag";

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
     * Sets creator
     *
     * @param creator
     */
    public void setCreator(ParseUser creator) {
        put(KEY_CREATOR, creator);
    }

    /**
     * Sets creatorRecord
     *
     * @param creator
     */
    public void setCreatorRecord(ParseUser creator) {
        put(KEY_CREATOR_RECORD, creator);
    }

    /**
     * Gets creator
     *
     * @return creator
     */
    public ParseUser getCreator() {
        return getParseUser(KEY_CREATOR);
    }

    /**
     * Gets creatorRecord
     *
     * @return creator
     */
    public ParseUser getCreatorRecord() {
        return getParseUser(KEY_CREATOR_RECORD);
    }

    /**
     * Sets receivers
     *
     * @param receivers
     */
    public void setReceiverList(List<ParseUser> receivers) {
        put(KEY_RECEIVER_LIST, receivers);
    }


    /**
     * Sets receiversRecord
     *
     * @param receivers
     */
    public void setReceiverRecord(List<ParseUser> receivers) {
        put(KEY_RECEIVER_RECORD, receivers);
    }

    /**
     * Gets receiver Record
     *
     * @return
     */
    public List<ParseUser> getReceiverRecord() {
        return getList(KEY_RECEIVER_RECORD);
    }

    /**
     * Gets receiver list
     *
     * @return
     */
    public List<ParseUser> getReceiverList() {
        return getList(KEY_RECEIVER_LIST);
    }



    public void setKeyWalkedUsersList(List<ParseWalkInfo> walkInfoList) {
        put(KEY_WALKED_USERS_LIST, walkInfoList);
    }

    public List<ParseWalkInfo> getKeyWalkedUsersList() {
        return getList(KEY_WALKED_USERS_LIST);
    }

    /**
     * Sets annotations
     *
     * @param annotations
     */
    public void setAnnotationList(List<ParseAnnotation> annotations) {
        put(KEY_ANNOTATION_LIST, annotations);
    }

    /**
     * Gets annotation list
     *
     * @return
     */
    public List<ParseAnnotation> getAnnotationList() {
        return getList(KEY_ANNOTATION_LIST);
    }

    /**
     * Sets description
     *
     * @param description
     */
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    /**
     * Sets tag
     */
    public void setTag(String tag) {
        put(KEY_TAG, tag);
    }


    /**
     * Gets description
     *
     * @return description
     */
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    /**
     * Gets query
     *
     * @return query
     */
    public static ParseQuery<ParseDrawing> getQuery() {
        return ParseQuery.getQuery(ParseDrawing.class);
    }

    @Override
    public int compareTo(ParseDrawing another) {
        if (this.isDataAvailable() && another.isDataAvailable()) {
            return -this.getCreatedAt().compareTo(another.getCreatedAt());
        }
        return 0;
    }


}
