package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.*
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.*

class WeatherEntityMapper : EntityMapper<WeatherMasterEntity, WeatherDTO> {
    override fun mapToEntity(dto: WeatherDTO): WeatherMasterEntity {
        return WeatherMasterEntity(
            lat = dto.lat,
            lon = dto.lon,
            timezone = dto.timezone,
            timezoneOffset = dto.timezoneOffset,
            current = CurrentEntityMapper().mapToEntity(dto.current ?: CurrentNetwork()),
            hourly = HourlyEntityMapper().toEntityList(dto.hourly),
            daily = DailyEntityMapper().toEntityList(dto.daily)
        )
    }


    class CurrentEntityMapper : EntityMapper<CurrentEntity, CurrentNetwork> {
        override fun mapToEntity(dto: CurrentNetwork): CurrentEntity {
            return CurrentEntity(
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
                weather = WeatherMapper().toEntityList(dto.weather)
            )
        }
    }

    class WeatherMapper : EntityMapper<WeatherEntity, WeatherNetwork> {
        override fun mapToEntity(dto: WeatherNetwork): WeatherEntity {
            return WeatherEntity(
                id = dto.id,
                main = dto.main,
                description = dto.description,
                icon = dto.icon
            )
        }

        fun toEntityList(initial: List<WeatherNetwork>): List<WeatherEntity> {
            return initial.map { mapToEntity(it) }
        }
    }

    class HourlyEntityMapper : EntityMapper<HourlyEntity, HourlyNetwork> {

        fun toEntityList(initial: List<HourlyNetwork>): List<HourlyEntity> {
            return initial.map { mapToEntity(it) }
        }

        override fun mapToEntity(dto: HourlyNetwork): HourlyEntity {
            return HourlyEntity(
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
                weather = WeatherMapper().toEntityList(dto.weather),
                pop = dto.pop
            )
        }
    }

    class TempEntityMapper : EntityMapper<TempEntity, TempNetwork> {
        override fun mapToEntity(dto: TempNetwork): TempEntity {
            return TempEntity(
                day = dto.day,
                min = dto.min,
                max = dto.max,
                night = dto.night,
                eve = dto.eve,
                morn = dto.morn
            )
        }

    }

    class FeelsLikeEntityMapper : EntityMapper<FeelsLikeEntity, FeelsLikeNetwork> {
        override fun mapToEntity(dto: FeelsLikeNetwork): FeelsLikeEntity {
            return FeelsLikeEntity(
                day = dto.day,
                night = dto.night,
                eve = dto.eve,
                morn = dto.morn
            )
        }
    }

    class DailyEntityMapper : EntityMapper<DailyEntity, DailyNetwork> {
        override fun mapToEntity(dto: DailyNetwork): DailyEntity {

            return DailyEntity(
                dt = dto.dt,
                sunrise = dto.sunrise,
                sunset = dto.sunset,
                moonrise = dto.moonrise,
                moonset = dto.moonset,
                moonPhase = dto.moonPhase,
                temp = TempEntityMapper().mapToEntity(dto.temp ?: TempNetwork()),
                feelsLike = FeelsLikeEntityMapper().mapToEntity(
                    dto.feelsLike ?: FeelsLikeNetwork()
                ),
                pressure = dto.pressure,
                humidity = dto.humidity,
                dewPoint = dto.dewPoint,
                windSpeed = dto.windSpeed,
                windDeg = dto.windDeg,
                windGust = dto.windGust,
                weather = WeatherMapper().toEntityList(dto.weather),
                clouds = dto.clouds,
                pop = dto.pop,
                rain = dto.rain,
                uvi = dto.uvi
            )
        }

        fun toEntityList(initial: List<DailyNetwork>): List<DailyEntity> {
            return initial.map { mapToEntity(it) }
        }

    }
}