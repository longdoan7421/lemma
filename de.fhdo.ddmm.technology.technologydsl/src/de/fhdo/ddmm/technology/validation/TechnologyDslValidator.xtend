/*
 * generated by Xtext 2.12.0
 */
package de.fhdo.ddmm.technology.validation

import org.eclipse.xtext.validation.Check
import de.fhdo.ddmm.technology.TechnologyPackage
import de.fhdo.ddmm.technology.Technology
import de.fhdo.ddmm.technology.CommunicationType
import de.fhdo.ddmm.technology.TechnologySpecificPrimitiveType
import de.fhdo.ddmm.data.PrimitiveType
import de.fhdo.ddmm.technology.DataFormat
import de.fhdo.ddmm.typechecking.TypecheckingUtils
import de.fhdo.ddmm.technology.CompatibilityMatrixEntry
import de.fhdo.ddmm.technology.CompatibilityDirection
import java.util.List

/**
 * This class contains custom validation rules.
 *
 * @author <a href="mailto:florian.rademacher@fh-dortmund.de>Florian Rademacher</a>
 */
class TechnologyDslValidator extends AbstractTechnologyDslValidator {
    /**
     * Check that there are not duplicates in the basic built-ins of a technology-specific primitive
     * type
     */
    @Check
    def checkBascBuiltinsUnique(TechnologySpecificPrimitiveType primitiveType) {
        // Note that we use the metamodel interfaces to check for duplicates. That is, because at
        // runtime, only their implementations are available (e.g., instead of PrimitiveBoolean
        // PrimitiveBooleanImpl).
        val basicBuiltins = primitiveType.basicBuiltinPrimitiveTypes.map[class.interfaces.get(0)]
        var PrimitiveType duplicate = null
        var Integer duplicateIndex = null

        // The following construct is just a "classic" search for element duplicates in two lists
        var i = 0
        while (i < basicBuiltins.size - 1 && duplicate === null) {
            val builtin = basicBuiltins.get(i)

            var n = i + 1
            while (n < basicBuiltins.size && duplicate === null) {
                val builtinDuplicateToCheck = basicBuiltins.get(n)

                if (builtinDuplicateToCheck == builtin) {
                    duplicate = primitiveType.basicBuiltinPrimitiveTypes.get(n)
                    duplicateIndex = n
                }

                n++
            }

            i++
        }

        if (duplicate !== null)
            error('''«duplicate.typeName» is already a basic type for «primitiveType.name»''',
                primitiveType, TechnologyPackage::Literals
                    .TECHNOLOGY_SPECIFIC_PRIMITIVE_TYPE__BASIC_BUILTIN_PRIMITIVE_TYPES,
                duplicateIndex
            )
    }

    /**
     * Check that there is only one technology-specific primitive type that is marked as the default
     * for a built-in primitive type. Otherwise, the code generator could not unambiguously decide
     * which technology-specific primitive type to use, when no explicit mapping of a built-in
     * primitive types was specified.
     */
    @Check
    def checkPrimitiveDefaultsUnique(TechnologySpecificPrimitiveType primitiveType) {
        if (!primitiveType.^default) {
            return
        }

        val otherDefaultPrimitiveTypes = primitiveType.technology.primitiveTypes
            .filter[^default && it !== primitiveType]
        val primitiveTypeBuiltins = primitiveType.basicBuiltinPrimitiveTypes
            .map[class.interfaces.get(0)]

        var TechnologySpecificPrimitiveType duplicateContainer = null
        var String duplicateName = null
        var Integer duplicateIndex = null
        var i = 0
        while (i < otherDefaultPrimitiveTypes.size && duplicateContainer === null) {
            val otherDefaultPrimitiveType = otherDefaultPrimitiveTypes.get(i)
            val otherBuiltins = otherDefaultPrimitiveType.basicBuiltinPrimitiveTypes
            var n = 0
            while (n < otherBuiltins.size && duplicateContainer === null) {
                val otherBuiltin = otherBuiltins.get(n).class.interfaces.get(0)
                if (primitiveTypeBuiltins.contains(otherBuiltin)) {
                    duplicateContainer = otherDefaultPrimitiveType
                    duplicateName = otherBuiltins.get(n).typeName
                    duplicateIndex = n
                }
                n++
            }
            i++
        }

        if (duplicateContainer !== null)
            error('''Duplicate default type: «primitiveType.name» is also specified as default ''' +
                '''type for built-in primitive «duplicateName»''',
                duplicateContainer, TechnologyPackage::Literals
                    .TECHNOLOGY_SPECIFIC_PRIMITIVE_TYPE__BASIC_BUILTIN_PRIMITIVE_TYPES,
                duplicateIndex
            )
    }

    /**
     * Check that technology defines at least one default technology-specific primitive type for
     * each built-in primitive type. This ensures, that even if there is no mapping of a built-in
     * primitive type to a technology-specific one, we can deduce a technology-specific type for it
     * when code gets generated.
     */
    @Check
    def checkPrimitiveDefaults(Technology technology) {
        /*
         * Get built-in primitive types of technology-specific primitive types, which are marked as
         * defaults for the basic built-in primitive types. Here, we map them to the metamodel
         * interfaces of the built-in primitive types. That is, because at runtime, instead of,
         * e.g., PrimitiveBoolean (which is a concept from the metamodel and an Ecore interface),
         * its implementing class PrimitiveBooleanImpl will be used.
         */
        val specificDefaultPrimitivesBasics = technology.primitiveTypes
            .filter[^default]
            .map[basicBuiltinPrimitiveTypes]
            .flatten
            .map[class.interfaces.get(0)]
            .toList

        if (specificDefaultPrimitivesBasics.empty) {
            error("Technology must define at least one default primitive type for each built-in " +
                  "primitive type", technology, TechnologyPackage::Literals.TECHNOLOGY__NAME)
            return
        }

        /*
         * Get list of all built-in primitive types, i.e., their metamodel interfaces. To be able
         * to retrieve the list from the metamodel, we need an instance of a PrimitiveType to be
         * able to call getBuiltinPrimitiveTypes() as Xcore does not allow static methods.
         */
        val primitiveTypeInstance = technology
            .primitiveTypes.get(0)
            .basicBuiltinPrimitiveTypes.get(0)
        val builtinPrimitives = primitiveTypeInstance.builtinPrimitiveTypes

        /*
         * Throw error if list of default, technology-specific primitive types' basic built-in
         * primitive types does not exhibit all built-in primitive types. That is, there are not
         * defaults defined for each built-in primitive type.
         */
        if (!specificDefaultPrimitivesBasics.containsAll(builtinPrimitives))
            error("Technology must define at least one default primitive type for each built-in " +
                  "primitive type", technology, TechnologyPackage::Literals.TECHNOLOGY__NAME)
    }

    /**
     * Check that there is exactly one default protocol for each communication type
     */
    @Check
    def checkProtocolDefaults(Technology technology) {
        checkProtocolsDefaults(technology, CommunicationType.SYNCHRONOUS)
        checkProtocolsDefaults(technology, CommunicationType.ASYNCHRONOUS)
    }

    /**
     * Convenience method to check protocol defaults
     */
    def checkProtocolsDefaults(Technology technology, CommunicationType forCommunicationType) {
        val communicationTypeString = switch (forCommunicationType) {
            case SYNCHRONOUS: "synchronous"
            case ASYNCHRONOUS: "asynchronous"
            default: ""
        }

        val definedDefaultProtocols = technology.protocols
            .filter[^default && communicationType == forCommunicationType]

        if (definedDefaultProtocols.empty)
            error('''Technology must define at least one default «communicationTypeString» ''' +
                '''protocol''', technology, TechnologyPackage::Literals.TECHNOLOGY__NAME)
        else if (definedDefaultProtocols.size >= 2)
            error('''Technology may not define more than one default «communicationTypeString» ''' +
                '''protocol''', definedDefaultProtocols.get(1),
                TechnologyPackage::Literals.PROTOCOL__NAME)
    }

    /**
     * Check that data formats are unique _within_ a protocol (which is the reason why we do not
     * consider data formats in the unique names validator, because we do not want them to be
     * globally unique within the whole technology model)
     */
    @Check
    def checkUniqueDataFormats(DataFormat dataFormat) {
        val allDataFormats = dataFormat.protocol.dataFormats
        var i = 0
        var duplicateFound = false
        var DataFormat currentFormat = null
        do {
            currentFormat = allDataFormats.get(i)
            duplicateFound = currentFormat != dataFormat &&
                currentFormat.formatName == dataFormat.formatName
            i++
        } while (currentFormat != dataFormat && !duplicateFound)

        if(duplicateFound)
            error ('''Duplicate data format «dataFormat.formatName»''', dataFormat,
                TechnologyPackage::Literals.DATA_FORMAT__FORMAT_NAME)
    }

    /**
     * Check if compatibility entries exhibit ambiguous entries or duplicates
     */
    @Check
    def checkCompatibilityMatrix(Technology technology) {
        if (technology.compatibilityEntries.empty) {
            return
        }

        val entrySet = <String> newHashSet
        technology.compatibilityEntries.forEach[entry |
            entry.compatibleTypes.forEach[compatibleType |
                val mappingTypeName = TypecheckingUtils.getTypeName(entry.mappingType)
                val compatibleTypeName = TypecheckingUtils.getTypeName(compatibleType)
                var ambiguousEntry = false
                var duplicateEntry = false

                /*
                 * The basic idea of the check is to first break down all entries to a consistent
                 * form following the pattern: "type can be converted to other_type". This
                 * corresponds to the semantics of the compatibility direction
                 * MAPPING_TO_COMPATIBLE_TYPES (->). Next, we check if such an entry already exists
                 * in an entry set. We have found an ambiguous entry if this is the case for
                 * BIDIRECTIONAL entries. Otherwise it's a duplicate entry, if the entry could not
                 * be added to the set.
                 */
                switch (entry.direction) {
                    // Match BIDIRECTIONAL entries to both directions, i.e., mapping type -> current
                    // compatible type and current compatible type -> mapping type.
                    case BIDIRECTIONAL:
                        ambiguousEntry = !entrySet.add(mappingTypeName + compatibleTypeName) ||
                            !entrySet.add(compatibleTypeName + mappingTypeName)
                    // COMPATIBLE_TYPES_TO_MAPPING entries become current compatible type ->
                    // mapping type
                    case COMPATIBLE_TYPES_TO_MAPPING:
                        duplicateEntry = !entrySet.add(compatibleTypeName + mappingTypeName)
                    // Default is MAPPING_TO_COMPATIBLE_TYPES: mapping type -> current compatible
                    //                                         type
                    default:
                        duplicateEntry = !entrySet.add(mappingTypeName + compatibleTypeName)
                }

                val errorMessage = if (ambiguousEntry)
                        "Ambiguous entry"
                    else if (duplicateEntry)
                        "Duplicate entry"
                    else
                        null

                if (errorMessage !== null)
                    error(errorMessage, entry,
                        TechnologyPackage::Literals.COMPATIBILITY_MATRIX_ENTRY__TECHNOLOGY)
            ]
        ]
    }

    /**
     * Warn, if an entry of the compatibility matrix, that maps two technology-specific primitive
     * types with basic built-in types, overrides built-in type conversion rules
     */
    @Check
    def checkCompatibilityEntryOverridesBuiltinCoompatibilityRules(CompatibilityMatrixEntry entry) {
        /* Only accept technology-specific primitive types with basic built-in primitives */
        if (!(entry.mappingType instanceof TechnologySpecificPrimitiveType)) {
            return
        }

        val mappingPrimitiveType = entry.mappingType as TechnologySpecificPrimitiveType
        if (mappingPrimitiveType.basicBuiltinPrimitiveTypes.empty) {
            return
        }

        val compatiblePrimitiveTypes = entry.compatibleTypes.filter[
            it instanceof TechnologySpecificPrimitiveType &&
            !(it as TechnologySpecificPrimitiveType).basicBuiltinPrimitiveTypes.empty
        ].map[it as TechnologySpecificPrimitiveType]
        .toList

        if (compatiblePrimitiveTypes.empty) {
            return
        }

        /*
         * The actual check consists of two steps:
         *     (1) Convert mapping into a canonical form, that enables to consistently call
         *         PrimitiveType.isCompatibleWith(). Therefore, the mapping entry is converted into
         *         a map. Each key corresponds to a primitive basic type, i.e., the "left side" of
         *         isCompatibleWith(). Each entry of the value list corresponds to a primitive type
         *         to check, i.e., the "right side" of compatibleWith(). We need a value list,
         *         because the mapping and compatible primitive types of the entry may exhibit more
         *         more than one basic built-in primitive type. That is, entries like
         *             primitive type DoubleFloat basic types double, float default;
         *             primitive type LongInt basic types int, long default;
         *             ...
         *             LongInt -> DoubleFloat;
         *         need to be converted (see the rules below) into a map
         *             float: int, long
         *             double: int, long
         *         resulting in the calls
         *             float.isCompatibleWith(int)
         *             float.isCompatibleWith(long)
         *             double.isCompatibleWith(int)
         *             double.isCompatibleWith(long)
         *         which all return true, making the compatibility entry obsolete because the
         *         built-in primitive types are all compatible already.
         *
         *         For this to work, the following rules are applied based on the compatibility
         *         entry's direction to convert a mapping entry into the described canonical
         *         representation within the map:
         *             - MAPPING_TO_COMPATIBLE_TYPES:
         *                  The mapping direction is "compatible type (ct) <- mapping type (mt)",
         *                  i.e., mapping can be converted into compatible type. Then, the canonical
         *                  form is "ct.isCompatibleWith(mt)" with ct=key and mt=value.
         *             - COMPATIBLE_TYPES_TO_MAPPING:
         *                  The mapping direction is "ct -> mt", i.e., compatible can be converted
         *                  into mapping type. Then, the canonical form is "mt.isCompatibleWith(ct)"
         *                  with mt=key and ct=value.
         *             - BIDIRECTIONAL:
         *                  Both entries "ct <- mt" (ct=key, mt=value) and "ct -> mt" (mt=key,
         *                  ct=value) are added to the map.
         *
         *     (2) Iterate over the map and call key.isCompatibleWith(current value from value
         *         list). If this returns true, collect overridden buit-in rules into a message
         *         string.
         *
         *     (3) Display warning that comprises all overridden built-in rules.
         */
        val overriddenDefaultsString = new StringBuilder
        compatiblePrimitiveTypes.forEach[
            // Build canonical representation map
            val compatibilityChecksTodo = buildCanonicalCompatibilityCheckMap(
                mappingPrimitiveType.basicBuiltinPrimitiveTypes, it.basicBuiltinPrimitiveTypes,
                entry.direction
            )

            // Perform actual compatibility checks
            compatibilityChecksTodo.forEach[basicType, typesToCheck | typesToCheck.forEach[
                typeToCheck |
                if (basicType.isCompatibleWith(typeToCheck))
                    overriddenDefaultsString.append(
                        '''«typeToCheck.typeName» to «basicType.typeName», '''
                    )
            ]]
        ]

        // Output message if overridden built-in rules were detected
        val overriddenStringLength = overriddenDefaultsString.length
        if (overriddenStringLength > 0) {
            val message = "Entry corresponds to built-in primitive conversion rules " +
                overriddenDefaultsString.toString.substring(0, overriddenStringLength-2)
            warning(message, entry,
                TechnologyPackage::Literals.COMPATIBILITY_MATRIX_ENTRY__TECHNOLOGY)
        }
    }

    /**
     * Helper method to build a map of a canonical representation for checking of a compatibility
     * matrix entry overrides a built-in type conversion rule
     */
    def buildCanonicalCompatibilityCheckMap(List<PrimitiveType> mappingTypes,
        List<PrimitiveType> compatibleTypes, CompatibilityDirection direction) {
        val canonicalCheckMap = <PrimitiveType, List<PrimitiveType>> newHashMap

        /*
         * From the perspective of the compatibility matrix, the canonical form is "key <- value",
         * i.e., can the value be converted into the key. This corresponds to the call
         * key.isCompatibleWith(value).
         * Therefore, the following rules are applied based on the compatibility entry's direction
         * to convert a mapping entry into the described canonical representation within the map:
         *     - MAPPING_TO_COMPATIBLE_TYPES:
         *         The mapping direction is "compatible type (ct) <- mapping type (mt)",
         *         i.e., mapping can be converted into compatible type. Then, the canonical
         *         form is "ct.isCompatibleWith(mt)" with ct=key and mt=value.
         *     - COMPATIBLE_TYPES_TO_MAPPING:
         *         The mapping direction is "ct -> mt", i.e., compatible can be converted
         *         into mapping type. Then, the canonical form is "mt.isCompatibleWith(ct)"
         *         with mt=key and ct=value.
         *     - BIDIRECTIONAL:
         *         Both entries "ct <- mt" (ct=key, mt=value) and "ct -> mt" (mt=key,
         *         ct=value) are added to the map.
         */
        mappingTypes.forEach[mappingType | compatibleTypes.forEach[compatibleType |
            if (direction === CompatibilityDirection.MAPPING_TO_COMPATIBLE_TYPES) {
                canonicalCheckMap.putIfAbsent(compatibleType, newArrayList)
                canonicalCheckMap.get(compatibleType).add(mappingType)
            } else if (direction === CompatibilityDirection.COMPATIBLE_TYPES_TO_MAPPING) {
                canonicalCheckMap.putIfAbsent(mappingType, newArrayList)
                canonicalCheckMap.get(mappingType).add(compatibleType)
            } else if (direction === CompatibilityDirection.BIDIRECTIONAL) {
                canonicalCheckMap.putIfAbsent(compatibleType, newArrayList)
                canonicalCheckMap.get(compatibleType).add(mappingType)

                canonicalCheckMap.putIfAbsent(mappingType, newArrayList)
                canonicalCheckMap.get(mappingType).add(compatibleType)
            }
        ]]

        return canonicalCheckMap
    }
}
