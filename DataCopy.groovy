/* code to export data from jdbc database */
import groovy.sql.Sql
import groovy.io.FileType
import java.sql.DatabaseMetaData
import java.sql.ResultSet


Map cfg = Props.load(new File("datacopy.properties"))

def srcProps=Props.sub(cfg,"source.")
def dstProps=Props.sub(cfg,"target.")

Sql.withInstance(srcProps){srcSql->
	println srcSql.firstRow("select sysdate() as SRC_DATE")
	Sql.withInstance(dstProps){dstSql->
		println srcSql.firstRow("select getdate() as DST_DATE")
		println "execute all pre scripts"
		new File("sql-pre").traverse(maxDepth:-1, type: FileType.FILES, filter: {it.name.endsWith(".sql")}, sort:{a,b-> a.toString() <=> b.toString() } ) {sqlf->
			SqlHelper.eachSemicolonCommand(sqlf){cmd->
				dstSql.execute(cmd)
			}
		}

		println "get table list"
		def tlist=[]
		if(cfg.'table.list'=='@source'){
			tlist = getTableList(srcSql, Props.sub(cfg,"tables."))
		}else if(cfg.'table.list'=='@target'){
			tlist = getTableList(dstSql, Props.sub(cfg,"tables."))
		}else{
			throw new Error("wrong value for `table.list` in export.properties")
		}
		//println tlist
		
		println "copy data"
		this.getClass().getClassLoader().loadClass(cfg."data.copy.class").newInstance().copy(cfg, srcSql, dstSql, tlist)
		//new DataCopyJDBC().copy(cfg, srcSql, dstSql, tlist)

		println "execute all post scripts"
		new File("sql-post").traverse(maxDepth:-1, type: FileType.FILES, filter: {it.name.endsWith(".sql")}, sort:{a,b-> a.toString() <=> b.toString() } ) {sqlf->
			SqlHelper.eachSemicolonCommand(sqlf){cmd->
				dstSql.execute(cmd)
			}
		}
	}
}

//returns a complex map :  [ [catalog:"", schema:"", name:"", columns:[name:[type:"",...  ]]] ]
List<Map<String,Object>> getTableList(Sql sql, Map filter){
	println filter
	def tlist = []
	def table = [:]
	def con = sql.getConnection()
	DatabaseMetaData md = con.getMetaData()
	ResultSet rs = md.getColumns(filter.catalog?:null, filter.schema?:null, "%", "%")
	while (rs.next()) {
		String n=rs.getString("TABLE_NAME")
		if(n=~filter.name){
			def catalog = rs.getString("TABLE_CAT")
			def schema = rs.getString("TABLE_SCHEM")
			if(n != table.name || catalog != table.catalog || schema != table.schema){
				table=[
					catalog:catalog,
					schema:schema,
					name:n,
					columns:[:]
				]
				tlist.add(table)
			}
			table.columns.put(rs.getString("COLUMN_NAME"),[
					type: rs.getInt("DATA_TYPE"),
					typeName: rs.getString("TYPE_NAME"),
					size: rs.getInt("COLUMN_SIZE"),
					scale: rs.getInt("DECIMAL_DIGITS"),
					isNullable: rs.getString("IS_NULLABLE")=="YES",
					isAutoincrement: rs.getString("IS_AUTOINCREMENT")=="YES",
				])
			//println rs.toRowResult().values()
		}
	}
	rs.close()
	return tlist
}
