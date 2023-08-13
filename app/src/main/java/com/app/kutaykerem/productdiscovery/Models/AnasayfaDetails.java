package com.app.kutaykerem.productdiscovery.Models;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

@Keep
public class AnasayfaDetails  implements Serializable  {



    public String name;
    public List<PcNames> categoryItemList;

    public AnasayfaDetails(String name, List<PcNames> categoryItemList) {
        this.name = name;
        this.categoryItemList = categoryItemList;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PcNames> getCategoryItemList() {
        return categoryItemList;
    }

    public void setCategoryItemList(List<PcNames> categoryItemList) {
        this.categoryItemList = categoryItemList;
    }


}
