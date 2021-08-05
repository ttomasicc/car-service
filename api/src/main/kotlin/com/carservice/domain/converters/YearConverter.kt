package com.carservice.domain.converters

import java.time.Year
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * Custom [Converter] for converting [Year] attribute to [Int] attribute, and vice versa
 */
@Converter(autoApply = true)
class YearConverter : AttributeConverter<Year, Int> {

    override fun convertToDatabaseColumn(attribute: Year): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): Year =
        Year.of(dbData)
}
