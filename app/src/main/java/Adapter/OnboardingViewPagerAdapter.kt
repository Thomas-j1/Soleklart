package Adapter

import onboarding.OnboardingData
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.himmeltitting.R

class OnboardingViewPagerAdapter(private var context : Context, private var onboardingDataList : List<OnboardingData>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {   // TODO: Her burde variablene få fargekode!
        return view == `object` // TODO: enkel eller dobbel = her? Veiledningen har ==
    }

    override fun getCount(): Int {
        return onboardingDataList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    // TODO: her er det noe feil. Det bør være flere fargekoder...?
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view : View = LayoutInflater.from(context).inflate(R.layout.onboarding_layout_screen, null)

        val onbImage : ImageView = view.findViewById(R.id.onbImage)
        val onbTitle : TextView = view.findViewById(R.id.onbTitle)
        val onbDesc : TextView = view.findViewById(R.id.onbDesc)

        onbImage.setImageResource(onboardingDataList[position].imageUrl)
        onbTitle.text = onboardingDataList[position].title
        onbDesc.text = onboardingDataList[position].desc

        container.addView(view)
        return view

    }
}
