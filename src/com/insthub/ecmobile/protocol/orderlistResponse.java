
package com.insthub.ecmobile.protocol;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.external.activeandroid.Model;
import com.external.activeandroid.annotation.Column;
import com.external.activeandroid.annotation.Table;

@Table(name = "orderlistResponse")
public class orderlistResponse  extends Model
{

     @Column(name = "status")
     public STATUS   status;

     public ArrayList<GOODORDER>   data = new ArrayList<GOODORDER>();

    @Column(name = "paginated")
    public PAGINATED   paginated;

     public void  fromJson(JSONObject jsonObject)  throws JSONException
     {
          if(null == jsonObject){
            return ;
           }

          JSONArray subItemArray;
          STATUS  status = new STATUS();
          status.fromJson(jsonObject.optJSONObject("status"));
          this.status = status;

          subItemArray = jsonObject.optJSONArray("data");
          if(null != subItemArray)
           {
              for(int i = 0;i < subItemArray.length();i++)
               {
                  JSONObject subItemObject = subItemArray.getJSONObject(i);
                  GOODORDER subItem = new GOODORDER();
                  subItem.fromJson(subItemObject);
                  this.data.add(subItem);
               }
           }
         PAGINATED  paginated = new PAGINATED();
         paginated.fromJson(jsonObject.optJSONObject("paginated"));
         this.paginated = paginated;
         return ;
     }

     public JSONObject  toJson() throws JSONException 
     {
          JSONObject localItemObject = new JSONObject();
          JSONArray itemJSONArray = new JSONArray();
          if(null != status)
          {
            localItemObject.put("status", status.toJson());
          }

          for(int i =0; i< data.size(); i++)
          {
              GOODORDER itemData =data.get(i);
              JSONObject itemJSONObject = itemData.toJson();
              itemJSONArray.put(itemJSONObject);
          }
          localItemObject.put("data", itemJSONArray);
         if(null != paginated)
         {
             localItemObject.put("paginated", paginated.toJson());
         }
          return localItemObject;
     }

}
