/*  */

class Props{
    public static Map load(def file){
        def p = [:]
        Cast.asReader(file,"UTF-8").eachLine{line->
            if(!line.startsWith("#") || line.trim().length()==0){
                (line=~"([^=]+)=(.*)").with{matcher->
                    if(matcher.find()){
                        String key=matcher.group(1)
                        String val=matcher.group(2)
                        p.put(key,val)
                    }
                }
            }
        }
        return p
    }
	/** collects properties with prefix and removes this prefix */
	def static sub(Map p, String prefix){
		return p.findAll{it.key.startsWith(prefix)}.collectEntries{k,v->[k.substring(prefix.length()),v]}
	}


}