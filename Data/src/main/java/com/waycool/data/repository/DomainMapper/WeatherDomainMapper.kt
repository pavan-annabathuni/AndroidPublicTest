package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.*
import com.waycool.data.repository.domainModels.*
import com.waycool.data.repository.domainModels.weather.DailyDomain
import com.waycool.data.repository.domainModels.weather.HourlyDomain
import com.waycool.data.repository.util.DomainMapper

class WeatherDomainMapper : DomainMapper<WeatherMasterDomain, WeatherMasterEntity> {
    override fun mapToDomain(dto: WeatherMasterEntity): WeatherMasterDomain {

        return WeatherMasterDomain(
            lat = dto.lat,
            lon = dto.lon,
            timezone = dto.timezone,
            timezoneOffset = dto.timezoneOffset,
            current = CurrentDomainMapper()
                .mapToDomain(dto.current ?: CurrentEntity()),
            hourly = HourlyDomainMapper().toDomainList(dto.hourly),
            daily = DailyDomainMapper().toDomainList(dto.daily)

        )
    }

    class CurrentDomainMapper : DomainMapper<CurrentDomain, CurrentEntity> {
        override fun mapToDomain(dto: CurrentEntity): CurrentDomain {
            return CurrentDomain(
                dt = dto.dt,
                sunrise = dto.sunrise,
                sunset = dto.sunset,
                temp = dto.temp,
                feelsLike = dto.feelsLike,
                pressure = dto.pressure,
                humidity = dto.humidity,
                dewPoint = dto.dewPoint,
                uvi = dto.uvi,
                clouds = dto.clouds,
                visibility = dto.visibility,
                windSpeed = dto.windSpeed,
                windDeg = dto.windDeg,
                weather = WeatherDomainMapper.WeatherMapper().toDomainList(dto.weather)
            )
        }
    }

    class WeatherMapper : DomainMapper<WeatherDomain, WeatherEntity> {
        fun toDomainList(initial: List<WeatherEntity>): List<WeatherDomain> {
            return initial.map { mapToDomain(it) }
        }

        override fun mapToDomain(dto: WeatherEntity): WeatherDomain {
            return WeatherDomain(
                id = dto.id,
                main = dto.main,
                description = dto.description,
                icon = dto.icon
            )
        }
    }

    class HourlyDomainMapper : DomainMapper<HourlyDomain, HourlyEntity> {

        fun toDomainList(initial: List<HourlyEntity>): List<HourlyDomain> {
            return initial.map { mapToDomain(it) }
        }


        override fun mapToDomain(dto: HourlyEntity): HourlyDomain {
            return HourlyDomain(
                dt = dto.dt,
                temp = dto.temp,
                feelsLike = dto.feelsLike,
                pressure = dto.pressure,
                humidity = dto.humidity,
                dewPoint = dto.dewPoint,
                uvi = dto.uvi,
                clouds = dto.clouds,
                visibility = dto.visibility,
                windSpeed = dto.windSpeed,
                windDeg = dto.windDeg,
                windGust = dto.windGust,
                weather = WeatherMapper().toDomainList(dto.weather),
                pop = dto.pop
            )
        }
    }

    class TempDomainMapper : DomainMapper<TempDomain, TempEntity> {

        override fun mapToDomain(dto: TempEntity): TempDomain {
            return TempDomain(
                day = dto.day,
                min = dto.min,
                max = dto.max,
                night = dto.night,
                eve = dto.eve,
                morn = dto.morn
            )
        }

    }

    class FeelsLikeDomainMapper : DomainMapper<FeelsLikeDomain, FeelsLikeEntity> {

        override fun mapToDomain(dto: FeelsLikeEntity): FeelsLikeDomain {
            return FeelsLikeDomain(
                day = dto.day,
                night = dto.night,
                eve = dto.eve,
                morn = dto.morn
            )
        }
    }

    class DailyDomainMapper : DomainMapper<DailyDomain, DailyEntity> {

        fun toDomainList(initial: List<DailyEntity>): List<DailyDomain> {
            return initial.map { mapToDomain(it) }
        }

        override fun mapToDomain(dto: DailyEntity): DailyDomain {
            return DailyDomain(
                dt = dto.dt,
                sunrise = dto.sunrise,
                sunset = dto.sunset,
                moonrise = dto.moonrise,
                moonset = dto.moonset,
                moonPhase = dto.moonPhase,
                temp = TempDomainMapper().mapToDomain(dto.temp ?: TempEntity()),
                feelsLike = FeelsLikeDomainMapper().mapToDomain(dto.feelsLike ?: FeelsLikeEntity()),
                pressure = dto.pressure,
                humidity = dto.humidity,
                dewPoint = dto.dewPoint,
                windSpeed = dto.windSpeed,
                windDeg = dto.windDeg,
                windGust = dto.windGust,
                weather = WeatherMapper().toDomainList(dto.weather),
                clouds = dto.clouds,
                pop = dto.pop,
                rain = dto.rain,
                uvi = dto.uvi
            )
        }

    }

}