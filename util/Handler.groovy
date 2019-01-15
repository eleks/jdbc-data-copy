/*  */
import groovy.sql.Sql;

class Handler{
	def static beforeTableCopy(Map cfg, Sql sql, def table){
		if(table.columns.find{name,defn->defn.isAutoincrement}){
			if(cfg."target.driver"=~/com\.microsoft.*/){
				sql.execute("SET IDENTITY_INSERT ${table.name} ON" as String)
			}
		}
		if(cfg."data.copy.handler.truncate" == "true"){
			sql.execute("truncate table ${table.name}" as String)
		}
	}

	def static afterTableCopy(Map cfg, Sql sql, def table){
		if(table.columns.find{name,defn->defn.isAutoincrement}){
			if(cfg."target.driver"=~/com\.microsoft.*/){
				sql.execute("SET IDENTITY_INSERT ${table.name} OFF" as String)
			}
		}
	}
}