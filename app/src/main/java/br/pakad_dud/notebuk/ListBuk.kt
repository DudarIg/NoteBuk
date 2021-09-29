package br.pakad_dud.notebuk

// дата класс для считывания данных из БД в адаптер MyAdapter
data class ListBuk(
        var id: Int =0,
        var title: String = "empty",
        var content: String = "empty",
        var uri: String = "empty",
        var time: String = "")