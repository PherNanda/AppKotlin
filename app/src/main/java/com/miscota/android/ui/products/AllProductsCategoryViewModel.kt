package com.miscota.android.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miscota.android.util.Event

class AllProductsCategoryViewModel : ViewModel() {

    private val _categories: MutableLiveData<List<AllProductsCategoryUiModel>> =
        MutableLiveData(listOf())
    val categories: LiveData<List<AllProductsCategoryUiModel>> = _categories

    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    fun loadSubCategories() {
        _categories.value = listOf(
            AllProductsCategoryUiModel.Header(
                categoryName = "Perros",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Comida",
                categoryUrl = "https://maskokotas.com/perros/c_comida",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Antiparasitaros",
                categoryUrl = "https://maskokotas.com/perros/c_antiparasitarios",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Higiene",
                categoryUrl = "https://maskokotas.com/perros/c_higiene-peluqueria",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Collares y correas",
                categoryUrl = "https://maskokotas.com/perros/c_collares-correas-arneses",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Transporte y viajes",
                categoryUrl = "https://maskokotas.com/perros/c_transporte-viaje",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Snacks",
                categoryUrl = "https://maskokotas.com/perros/c_snacks",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Descanso",
                categoryUrl = "https://maskokotas.com/perros/c_descanso",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Complementos y sumplementos",
                categoryUrl = "https://maskokotas.com/perros/c_complementos-suplementos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Juguetes",
                categoryUrl = "https://maskokotas.com/perros/c_juguetes",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Ropa",
                categoryUrl = "https://maskokotas.com/perros/c_ropa",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Adiestramiento",
                categoryUrl = "https://maskokotas.com/perros/c_adiestramiento",
            ),
            AllProductsCategoryUiModel.Header(
                categoryName = "Gatos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Comida",
                categoryUrl = "https://maskokotas.com/gatos/c_comida",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Arena",
                categoryUrl = "https://maskokotas.com/gatos/c_arena-para-gatos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Higiene",
                categoryUrl = "https://maskokotas.com/gatos/c_peluqueria-higiene",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Rascadores",
                categoryUrl = "https://maskokotas.com/gatos/c_rascadores",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Juguetes",
                categoryUrl = "https://maskokotas.com/gatos/c_juguetes",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Snacks",
                categoryUrl = "https://maskokotas.com/gatos/c_snacks",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Antiparasitarios",
                categoryUrl = "https://maskokotas.com/gatos/c_antiparasitarios",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Transporte y viaje",
                categoryUrl = "https://maskokotas.com/gatos/c_transporte-viaje",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Comederos y bebederos",
                categoryUrl = "https://maskokotas.com/gatos/c_comederos-bebederos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Complementos y suplementos",
                categoryUrl = "https://maskokotas.com/gatos/c_complementos-suplementos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Descanso",
                categoryUrl = "https://maskokotas.com/gatos/c_descanso",
            ),
            AllProductsCategoryUiModel.Header(
                categoryName = "Roedores",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Piensos y mezclas ",
                categoryUrl = "https://maskokotas.com/roedores/c_piensos-mezclas",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Henos ",
                categoryUrl = "https://maskokotas.com/roedores/c_henos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Snacks ",
                categoryUrl = "https://maskokotas.com/roedores/c_snacks",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Complementos ",
                categoryUrl = "https://maskokotas.com/roedores/c_complementos-alimenticios",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Lechos y sustratos ",
                categoryUrl = "https://maskokotas.com/roedores/c_lechos-sustratos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Higiene ",
                categoryUrl = "https://maskokotas.com/pajaros/c_accesorios-para-jaulas",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Jaulas y parques",
                categoryUrl = "https://maskokotas.com/roedores/c_jaulas-parques",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Accesorios para jaulas ",
                categoryUrl = "https://maskokotas.com/pajaros/c_accesorios-para-jaulas",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Juguetes",
                categoryUrl = "https://maskokotas.com/roedores/c_juguetes",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Correas y arneses ",
                categoryUrl = "https://maskokotas.com/roedores/c_juguetes",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Transportines ",
                categoryUrl = "https://maskokotas.com/roedores/c_transportines",
            ),
            AllProductsCategoryUiModel.Header(
                categoryName = "Reptiles",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Comida ",
                categoryUrl = "https://maskokotas.com/reptiles/c_comida",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Complementos ",
                categoryUrl = "https://maskokotas.com/reptiles/c_complementos-alimenticios",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Lechos y sustratos ",
                categoryUrl = "https://maskokotas.com/reptiles/c_lechos-sustratos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Higiene ",
                categoryUrl = "https://maskokotas.com/reptiles/c_higiene-limpieza",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Terrarios  ",
                categoryUrl = "https://maskokotas.com/reptiles/c_terrarios",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Tortugueras ",
                categoryUrl = "https://maskokotas.com/reptiles/c_tortugueras",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Iluminación ",
                categoryUrl = "https://maskokotas.com/reptiles/c_iluminacion",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Comederos y bebederos ",
                categoryUrl = "https://maskokotas.com/reptiles/c_comederos-bebederos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Decoración ",
                categoryUrl = "https://maskokotas.com/reptiles/c_decoracion",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Calefacción ",
                categoryUrl = "https://maskokotas.com/reptiles/c_calefaccion",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Humedad  ",
                categoryUrl = "https://maskokotas.com/reptiles/c_humedad",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Medición y regulación",
                categoryUrl = "https://maskokotas.com/reptiles/c_medicion-regulacion",
            ),
            AllProductsCategoryUiModel.Header(
                categoryName = "Pájaros",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Comida ",
                categoryUrl = "https://maskokotas.com/pajaros/c_comida-para-pajaros",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Papillas ",
                categoryUrl = "https://maskokotas.com/pajaros/c_papillas-pastas-de-cria",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Snacks ",
                categoryUrl = "https://maskokotas.com/pajaros/c_snacks",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Complementos y suplementos ",
                categoryUrl = "https://maskokotas.com/pajaros/c_complementos-suplementos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Lechos y sustratos ",
                categoryUrl = "https://maskokotas.com/pajaros/c_lechos-sustratos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Higiene",
                categoryUrl = "https://maskokotas.com/pajaros/c_higiene-limpieza",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Pajareras ",
                categoryUrl = "https://maskokotas.com/pajaros/c_jaulas-pajareras",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Accesorios para pajareras",
                categoryUrl = "https://maskokotas.com/pajaros/c_accesorios-para-jaulas",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Comederos y bebederos",
                categoryUrl = "https://maskokotas.com/pajaros/c_comederos-bebederos-para-pajaros",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Juguetes ",
                categoryUrl = "https://maskokotas.com/pajaros/c_juguetes",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Gallinas ",
                categoryUrl = "https://maskokotas.com/pajaros/c_gallinas",
            ),
            AllProductsCategoryUiModel.Header(
                categoryName = "Peces",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Comida ",
                categoryUrl = "https://maskokotas.com/peces/c_comida-para-peces",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Acuarios ",
                categoryUrl = "https://maskokotas.com/peces/c_acuarios",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Peceras  ",
                categoryUrl = "https://maskokotas.com/peces/c_peceras",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Filtros y bombas",
                categoryUrl = "https://maskokotas.com/peces/c_filtros-bombasc",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Iluminación ",
                categoryUrl = "https://maskokotas.com/peces/c_iluminacion",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Accesorios acuarios ",
                categoryUrl = "https://maskokotas.com/peces/c_accesorios-acuarios",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Decoración ",
                categoryUrl = "https://maskokotas.com/peces/c_decoracion",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Tratamiento y mantenimiento",
                categoryUrl = "https://maskokotas.com/peces/c_tratamiento-mantenimiento",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Mantenimiento de plantas ",
                categoryUrl = "https://maskokotas.com/peces/c_tratamiento-mantenimiento",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Estanques ",
                categoryUrl = "https://maskokotas.com/peces/c_estanques",
            ),
            AllProductsCategoryUiModel.Header(
                categoryName = "Caballos",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Alimentación  ",
                categoryUrl = "https://maskokotas.com/caballos/c_alimentacion",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Complementos alimenticios",
                categoryUrl = "https://maskokotas.com/caballos/c_complementos-alimenticios",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Higiene y cuidados ",
                categoryUrl = "https://maskokotas.com/caballos/c_higiene-cuidados",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Antiparasitarios y repelentes",
                categoryUrl = "https://maskokotas.com/caballos/c_antiparasitarios-repelentes",
            ),
            AllProductsCategoryUiModel.SubCategoryUiModel(
                categoryName = "Comederos y bebederos",
                categoryUrl = "https://maskokotas.com/caballos/s_comederos-bebederos",
            ),
        )
    }

}