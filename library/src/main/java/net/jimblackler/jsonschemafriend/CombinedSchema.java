package net.jimblackler.jsonschemafriend;

import static net.jimblackler.jsonschemafriend.TypeInferrer.inferTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CombinedSchema {
  private final Schema schema;

  public CombinedSchema(Schema schema) {
    this.schema = schema;
  }

  public Collection<String> getInferredTypes() {
    Collection<String> allTypes = inferTypes(schema);

    Collection<Schema> anyOf = schema.getAnyOf();
    if (anyOf != null && !anyOf.isEmpty()) {
      // For the anyOf operator we return the intersection of the types (implied or explicit) of
      // each subschema.
      for (Schema subSchema : anyOf) {
        allTypes.addAll(inferTypes(subSchema));
      }
    }

    Collection<Schema> allOf = schema.getAllOf();
    if (!allOf.isEmpty()) {
      // For the allOf operator we return the union of the types (implied or explicit) of each
      // subschema.
      for (Schema subSchema : allOf) {
        Collection<String> types = inferTypes(subSchema);
        allTypes.retainAll(types);
      }
    }

    return allTypes;
  }

  public Map<String, Schema> getProperties() {
    Map<String, Schema> allProperties = new HashMap<>(schema.getProperties());

    Collection<Schema> anyOf = schema.getAnyOf();
    if (anyOf != null && !anyOf.isEmpty()) {
      for (Schema subSchema : anyOf) {
        allProperties.putAll(subSchema.getProperties());
      }
    }

    Collection<Schema> allOf = schema.getAllOf();
    if (!allOf.isEmpty()) {
      for (Schema subSchema : allOf) {
        allProperties.putAll(subSchema.getProperties());
      }
    }

    return allProperties;
  }

  public Collection<String> getNonProhibitedTypes() {
    Collection<String> allTypes = new HashSet<>(TypeInferrer.getNonProhibitedTypes(schema));

    Collection<Schema> anyOf = schema.getAnyOf();
    if (anyOf != null && !anyOf.isEmpty()) {
      // For the anyOf operator we return the intersection of the types of each subschema.
      for (Schema subSchema : anyOf) {
        allTypes.addAll(TypeInferrer.getNonProhibitedTypes(subSchema));
      }
    }

    Collection<Schema> allOf = schema.getAllOf();
    if (!allOf.isEmpty()) {
      // For the anyOf operator we return the union of the types of each subschema.
      for (Schema subSchema : allOf) {
        Collection<String> types = TypeInferrer.getNonProhibitedTypes(subSchema);
        allTypes.retainAll(types);
      }
    }

    return allTypes;
  }

  public Collection<String> getExplicitTypes() {
    Collection<String> allTypes = new HashSet<>();
    Collection<String> explicitTypes = schema.getExplicitTypes();
    if (explicitTypes != null) {
      allTypes.addAll(explicitTypes);
    }

    Collection<Schema> anyOf = schema.getAnyOf();
    if (anyOf != null && !anyOf.isEmpty()) {
      // For the anyOf operator we return the intersection of the types of each subschema.
      for (Schema subSchema : anyOf) {
        allTypes.addAll(subSchema.getExplicitTypes());
      }
    }

    Collection<Schema> allOf = schema.getAllOf();
    if (!allOf.isEmpty()) {
      // For the anyOf operator we return the union of the types of each subschema.
      for (Schema subSchema : allOf) {
        Collection<String> types = subSchema.getExplicitTypes();
        allTypes.retainAll(types);
      }
    }

    return allTypes;
  }

  public Collection<Object> getEnums() {
    Collection<Object> allEnums = new HashSet<>();
    List<Object> enums1 = schema.getEnums();
    if (enums1 != null) {
      allEnums.addAll(enums1);
    }

    Collection<Schema> anyOf = schema.getAnyOf();
    if (anyOf != null && !anyOf.isEmpty()) {
      // For the anyOf operator we return the intersection of the types of each subschema.
      for (Schema subSchema : anyOf) {
        List<Object> enums = subSchema.getEnums();
        if (enums != null) {
          allEnums.addAll(enums);
        }
      }
    }

    Collection<Schema> allOf = schema.getAllOf();
    if (!allOf.isEmpty()) {
      // For the anyOf operator we return the union of the types of each subschema.
      for (Schema subSchema : allOf) {
        Collection<Object> enums = subSchema.getEnums();
        if (enums != null) {
          allEnums.retainAll(enums);
        }
      }
    }
    if (allEnums.isEmpty()) {
      return null;
    }
    return allEnums;
  }
}
