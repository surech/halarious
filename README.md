Halarious
=========
halarious is a simple framework for serializing and deserializing Java-Classes to JSON following the HAL-specification. 
- HAL is a simple format that gives a consistent and easy way to hyperlink between resources in your API.
- halarious is based on GSON, a lightweight library by Google.
- References and embedded fields can simply be marked with annotations. No further configuration nedded.

Check out our [Website](http://www.halarious.ch) for more informations!

POJO with HAL-annotations:
```java
public class ProductResource implements HalResource {
    @HalLink
    private String self;
    private String name;
    private int weight;
    private String description;
    @HalEmbedded
    private ManufacturerResource manufacturer;
 
    /** Getter/Setter... */
}
 
public class ManufacturerResource implements HalResource {
    @HalLink
    private String self;
    @HalLink
    private String homepage;
    private String name;
 
    /** Getter/Setter... */
}
```

This generates the following json:
```javascript
{
    "name": "A product",
    "weight": 400,
    "description": "A great product",
    "_links": {
        "self": {
            "href": "/product/987"
        }
    },
    "_embedded": {
        "manufacturer": {
            "name": "http://hoverdonkey.com",
            "_links": {
                "self": {
                    "href": "/manufacturers/328764"
                },
                "homepage": {
                    "href": "Manufacturer Inc."
                }
            }
        }
    }
}
```

### Licence

halarious is available under the [Apache 2.0 Licence](http://www.apache.org/licenses/LICENSE-2.0).
