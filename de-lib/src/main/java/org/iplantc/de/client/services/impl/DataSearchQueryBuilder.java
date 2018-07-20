package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.sharing.PermissionValue;

import com.google.common.collect.Lists;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.List;
import java.util.logging.Logger;

/**
 * This class uses a builder pattern to construct a search query from a given query template.
 *
 * If a field in the given query template is null or empty, the corresponding search term will be omitted
 * from the final query.
 *
 * @author aramsey
 */
@SuppressWarnings("nls")
public class DataSearchQueryBuilder {

    // QUERY BUILDING BLOCKS
    public static final String ALL = "all";
    public static final String ANY = "any";
    public static final String NONE = "none";

    public static final String TYPE = "type";
    public static final String ARGS = "args";
    public static final String QUERY = "query";
    public static final String EXACT = "exact";


    // CLAUSES

    //Label
    public static final String LABEL = "label";
    //Path
    public static final String PATH = "path";
    public static final String PREFIX = "prefix";
    //Owner
    public static final String OWNER = "owner";
    //Permissions
    public static final String PERMISSIONS = "permissions";
    public static final String PERMISSION = "permission";
    public static final String PERMISSION_RECURSE = "permission_recurse";
    public static final String SHARED_WITH = "users";
    //Tag
    public static final String TAG = "tag";
    public static final String TAGS = "tags";
    //Metadata
    public static final String METADATA = "metadata";
    public static final String ATTRIBUTE = "attribute";
    public static final String ATTRIBUTE_EXACT = "attribute_exact";
    public static final String VALUE = "value";
    public static final String VALUE_EXACT = "value_exact";
    public static final String UNIT = "unit";
    public static final String UNIT_EXACT = "unit_exact";
    public static final String METADATA_TYPES = "metadata_types";
    //Date ranges
    public static final String SIZE = "size";
    public static final String FROM = "from";
    public static final String TO = "to";


    //Modified within
    public static final String MODIFIED = "modified";
    //Created within
    public static final String CREATED = "created";

    // Values only used in Query Builder UI
    public static final String OPLABEL = "opLabel";
    public static final String NEGATED = "negated";


    private final Splittable template;
    private final Splittable query;

    Logger LOG = Logger.getLogger(DataSearchQueryBuilder.class.getName());

    public DataSearchQueryBuilder(Splittable template) {
        this.template = template.deepCopy();
        this.query = StringQuoter.createSplittable();
    }

    public Splittable buildQuery() {
        buildQuery(query, template);
        return query;
    }

    public void buildQuery(Splittable parent, Splittable child) {
        Splittable noneList = StringQuoter.createIndexed();
        if (isGroup(child)) {
            String type = getTypeValue(child);
            Splittable group = StringQuoter.createSplittable();
            Splittable arrayArgs = getArgs(child);
            if (arrayArgs != null) {
                Splittable newArrayArgs = StringQuoter.createIndexed();
                assignKeyValue(group, type, newArrayArgs);
                for (int i = 0; i < arrayArgs.size(); i++) {
                    Splittable arg = arrayArgs.get(i);
                    buildQuery(newArrayArgs, arg);
                }
                child = group;
                if (parent.isIndexed()) {
                    appendArrayItem(parent, child);
                } else {
                    assignKeyValue(parent, QUERY, child);
                }
            }
        } else {
            String type = getTypeValue(child);
            if (type == null) {
                return;
            }
            switch (type) {
                case SIZE:
                    fileSizeRange(child);
                    break;
                case TAG:
                    tags(child);
                    break;
            }
            boolean negated = isNegated(getArgs(child));
            if (negated) {
                Splittable noneNamed = StringQuoter.createSplittable();
                assignKeyValue(noneNamed, NONE, noneList);
                appendArrayItem(noneList, child);
                appendArrayItem(parent, noneNamed);
            } else {
                appendArrayItem(parent, child);
            }
        }
    }

    private boolean isNegated(Splittable arg) {
        Splittable negated = arg.get(NEGATED);
        return negated != null && negated.asBoolean();
    }

    /**
     * {"type": "size", "args": {"from": "1KB", "to": "2KB"}}
     */
    public void fileSizeRange(Splittable child) {
        Splittable fileSizes = getArgs(child);
        if (fileSizes != null && (!fileSizes.isNull(FROM) || !fileSizes.isNull(TO))) {
            Splittable from = fileSizes.get(FROM);
            Splittable to = fileSizes.get(TO);
            if (from != null && !from.isNull("value") && !from.isNull("unit")) {
                assignKeyValue(fileSizes, FROM, from.get("value").asNumber() + from.get("unit").asString());
            } else {
                assignKeyValue(fileSizes, FROM, "");
            }
            if (to != null && !to.isNull("value") && !to.isNull("unit")) {
                assignKeyValue(fileSizes, TO, to.get("value").asNumber() + to.get("unit").asString());
            } else {
                assignKeyValue(fileSizes, TO, "");
            }
        }
    }

    /**
     * {"type": "tag", "args": {"tags": ["UUID_1","UUID_2","UUID_3"]}
     */
    public void tags(Splittable child) {
        Splittable args = getArgs(child);
        if (args != null && !args.isNull(TAGS)) {
            Splittable tags = args.get(TAGS);
            Splittable tagIds = tagToString(tags);
            assignKeyValue(args, TAGS, tagIds);
        }
    }

    private Splittable tagToString(Splittable tags) {
        Splittable tagIds = StringQuoter.createIndexed();
        if (tags.isIndexed()) {
            for (int i = 0; i < tags.size(); i++) {
                Splittable tag = tags.get(i);
                appendArrayItem(tagIds, tag.get("id"));
            }
        }
        return tagIds;
    }

    /**
     * {"type": "typeVal", "args": {"argKey": "argVal"}}
     */
    public Splittable createTypeClause(String typeVal, String argKey, String argVal) {
        Splittable argKeySpl = StringQuoter.createSplittable();
        assignKeyValue(argKeySpl, argKey, argVal);
        return createTypeClause(typeVal, argKeySpl);
    }

    /**
     * {"type": "typeVal", "args": {"argKey": "argVal"}}
     */
    public Splittable createTypeClause(String typeVal, String argKey, Boolean argVal) {
        Splittable argKeySpl = StringQuoter.createSplittable();
        assignKeyValue(argKeySpl, argKey, argVal);
        return createTypeClause(typeVal, argKeySpl);
    }

    /**
     * {"type": "typeVal", "args": {"argKey": argVal}}
     */
    public Splittable createTypeClause(String typeVal, String argKey, Splittable argVal) {
        Splittable argKeySpl = StringQuoter.createSplittable();
        assignKeyValue(argKeySpl, argKey, argVal);
        return createTypeClause(typeVal, argKeySpl);
    }

    /**
     * {"type": "typeVal", "args": {"argKey": "argVal"}}
     */
    private Splittable createTypeClause(String typeVal, Splittable args) {
        Splittable type = StringQuoter.createSplittable();

        assignKeyValue(type, TYPE, typeVal);
        assignKeyValue(type, ARGS, args);

        return type;
    }

    /**
     * Takes a splittable, assigns the specified key and value
     * { "key" : "value" }
     * @param keySplittable
     * @param key
     * @param value
     */
    private void assignKeyValue(Splittable keySplittable, String key, String value) {
        StringQuoter.create(value).assign(keySplittable, key);
    }

    /**
     * Takes a splittable, assigns the specified key and value
     * { "key" : { "someKey" : "someValue" } }
     * @param keySplittable
     * @param key
     * @param value
     */
    private void assignKeyValue(Splittable keySplittable, String key, Splittable value) {
        value.assign(keySplittable, key);
    }

    /**
     * Takes a splittable, assigns the specified key and boolean value
     * { "key" : true }
     * @param keySplittable
     * @param key
     * @param value
     */
    private void assignKeyValue(Splittable keySplittable, String key, boolean value) {
        StringQuoter.create(value).assign(keySplittable, key);
    }

    /**
     * Takes a splittable array and adds the specified item to it
     * @param array
     * @param item
     */
    private void appendArrayItem(Splittable array, Splittable item) {
        item.assign(array, array.size());
    }

    private boolean isGroup(Splittable spl) {
        String type = getTypeValue(spl);
        return groups().contains(type);
    }


    private String getTypeValue(Splittable spl) {
        Splittable type = spl.get(TYPE);
        if (type != null) {
            return type.asString();
        }
        return null;
    }

    // Retrieves either the array of {type, arg} objects expected with a group
    // or the args object expected with a condition
    private Splittable getArgs(Splittable spl) {
        Splittable args = spl.get(ARGS);
        if (args != null) {
            return args;
        }
        return null;
    }

    private List<String> groups() {
        return Lists.newArrayList(ALL, ANY, NONE);
    }

    /**
     * @return the currently constructed query.
     */
    public String toString(Splittable query) {
        return query.getPayload();
    }
}
