package com.fufu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private String img;
    private String title;
    private String price;

    public void setItem(Content content, String key, String value){
        if(key == "img"){
            content.setImg(value);
        }
        if(key == "title"){
            content.setTitle(value);
        }
        if(key == "price"){
            content.setPrice(value);
        }
    }
}
