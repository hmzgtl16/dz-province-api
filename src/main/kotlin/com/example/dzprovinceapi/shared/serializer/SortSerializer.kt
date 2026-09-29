package com.example.dzprovinceapi.shared.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import org.springframework.data.domain.Sort

class SortSerializer : KSerializer<Sort> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Sort") {
            element<String>("empty")
            element<String>("sorted")
            element<String>("unsorted")
        }

    override fun serialize(
        encoder: Encoder,
        value: Sort,
    ) {
        encoder.encodeStructure(descriptor) {
            encodeBooleanElement(descriptor, 0, value.isEmpty)
            encodeBooleanElement(descriptor, 1, value.isSorted)
            encodeBooleanElement(descriptor, 2, value.isUnsorted)
        }
    }

    override fun deserialize(decoder: Decoder): Sort =
        throw UnsupportedOperationException("Deserializing Sort is not supported")
}
