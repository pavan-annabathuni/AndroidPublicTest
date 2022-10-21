package com.waycool.weather.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.waycool.data.repository.domainModels.weather.DailyDomain
import com.waycool.weather.R
import com.waycool.weather.databinding.ItemDailyBinding
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter(val onClickListener:OnClickListener):androidx.recyclerview.widget.ListAdapter<DailyDomain,WeatherAdapter.ViewHolder>(DiffCallback) {


    class ViewHolder(private var binding: ItemDailyBinding):
    RecyclerView.ViewHolder(binding.root){
        fun bind(data:DailyDomain){
         binding.property = data
            binding.executePendingBindings()
        }
        val x = binding.tvDays
        val cv = binding.cardView3
        val alerts = binding.tvTextAlert

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val properties = getItem(position)
        holder.bind(properties)
        holder.bind(properties)
        val date = properties.dt?.times(1000L)
        val dateTime=Date()
        if (date != null) {
            dateTime.time=date
        }
        val formatter = SimpleDateFormat("EE, d MMMM",Locale.ENGLISH)
        val formatDate = formatter.format(dateTime)
        holder.x.text = formatDate

         holder.itemView.setOnClickListener(){
             onClickListener.clickListener(properties)
         }
        val yellow = "#070D09"
        val lightYellow = "#FFFAF0"
        val red = "#FF2C23"
        val lightRed = "#FFD7D0"
        val green = "#08FA12"
        val lightGreen = "#08FA12"
        when(properties.weather[0].id){
            200-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            201-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            202-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            210-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            211-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            212-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            221-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            230-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            231-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            232-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            300-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            301-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            302-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            310-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            311-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            312-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            313-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            314-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            321-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            500-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            501-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            502-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            503-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            504-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            511-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            520-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            521-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            522-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            531-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(red))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightRed))
            }
            701-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            711-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            721-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            731-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            741-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            751-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            761-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            800-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(green))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightGreen))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation,0,0,0)

            }
            801-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(green))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightGreen))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation,0,0,0)
            }
            802-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(green))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightGreen))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation,0,0,0)
            }
            803-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }
            804-> {holder.alerts.text = properties.weather[0].description
                holder.alerts.setTextColor(Color.parseColor(yellow))
                holder.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                holder.alerts.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_circle_exclamation_brown,0,0,0)
            }

        }

    }

//    override fun getItemCount(): Int {
//        return 7
//    }

    companion object DiffCallback : DiffUtil.ItemCallback<DailyDomain>() {

        override fun areItemsTheSame(oldItem: DailyDomain, newItem: DailyDomain): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: DailyDomain, newItem:DailyDomain): Boolean {
            return oldItem.temp == newItem.temp
        }
        class OnClickListener(val clickListener: (data: DailyDomain) -> Unit) {
            fun onClick(data:DailyDomain) = clickListener(data)
        }
    }

}