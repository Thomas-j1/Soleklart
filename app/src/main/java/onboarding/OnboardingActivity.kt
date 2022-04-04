package onboarding

import Adapter.OnboardingViewPagerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.viewpager.widget.ViewPager
import com.example.himmeltitting.R
import com.google.android.material.tabs.TabLayout


class OnboardingActivity {

// TODO: Kode skrevet etter denne veiledningen: https://www.youtube.com/watch?v=gEt8QpNfjMA
// TODO: Gjøre om til binding?
// TODO: Sette inn bilder av knapper/ screenshots
// TODO: rødelinjer og røde ord...


    var onboardingViewPagerAdapter : OnboardingViewPagerAdapter? = null
    var tabLayout: TabLayout? = null
    var onboardingViewPager : ViewPager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tab_indicator)
    }

    private fun setOnboardingViewPagerAdapter(onboardingData: List<OnboardingData>){
        onboardingViewPager = findViewById(R.id.screenPager)
        onboardingViewPagerAdapter = OnboardingViewPagerAdapter(this, onboardingData)
        onboardingViewPager!!.adapter = onboardingViewPagerAdapter
        tabLayout?.setupWithViewPager(onboardingViewPager)

        val onboardingData : MutableList<OnboardingData> = ArrayList()
        onboardingData.add(OnboardingData("Hva er Soleklart?", "Soleklart gir deg tidspunkt og værvarsel for både soloppgang og solnedgang, slik at du kan planlegge flotte opplevelser!", R.drawable.logo_g_stor))
        onboardingData.add(OnboardingData("Navigasjon", "Du navigerer i appen ved hjelp av menyen nederst på skjermen.", R.drawable.logo_g_stor))
        onboardingData.add(OnboardingData("Søk", "Søk etter værvarsel på ønsket lokasjon. Du kan søke med tekst, ved å trykke i kartet, eller ved å bruke GPS-posisjonen din.", R.drawable.logo_g_stor))
        onboardingData.add(OnboardingData("Værvarsel", "Du får oppgitt hvor mye skyer det er, temperatur, nedbør, vind og mengden svevestøv.", R.drawable.logo_g_stor))
        onboardingData.add(OnboardingData("Favoritter", "Du kan lagre favorittstedene dine ved å trykke på hjertet. Du velger selv hva posisjonen skal hete, og du sletter dem ved å trykke på hjertet en gang til.", R.drawable.logo_g_stor))
        onboardingData.add(OnboardingData("Innstillinger", "Her kan du aktivere dark mode, og endre språk.", R.drawable.logo_g_stor))


        setOnboardingViewPagerAdapter(onboardingData)

    }
}