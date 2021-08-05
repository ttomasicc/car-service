package com.carservice.unit.services

import com.carservice.domain.CarName
import com.carservice.models.views.CarNameView
import com.carservice.repositories.CarNameRepository
import com.carservice.services.InfoService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

@ExtendWith(MockKExtension::class)
class InfoServiceUnitTest {

    @MockK
    lateinit var carNameRepository: CarNameRepository

    @InjectMockKs
    lateinit var infoService: InfoService

    @Test
    fun `retrieving car names should return paged car names`() {
        val page: Page<CarName> = PageImpl(
            listOf(
                CarName(1234, "Porsche", "Cayenne"),
                CarName(1240, "Porsche", "Karisma")
            )
        )

        every {
            carNameRepository.findAll(Pageable.unpaged())
        } returns page

        assertThat(infoService.findAllCarNames(Pageable.unpaged()))
            .isEqualTo(
                page.map {
                    CarNameView(it)
                }
            )
    }

    @Test
    fun `retrieving used car names should return list of used car names`() {
        val carName = CarName(2000, "Oldsmobile", "Starfire")

        every {
            carNameRepository.findUsedCarNames()
        } returns listOf(carName)

        assertThat(infoService.findUsedCarNames())
            .isEqualTo(listOf(CarNameView(carName)))
    }
}
