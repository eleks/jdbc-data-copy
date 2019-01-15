/*   */
import groovy.sql.Sql;
import groovy.sql.GroovyResultSet;
import groovy.sql.BatchingPreparedStatementWrapper;

@groovy.transform.CompileStatic /* apply statis types check to run faster */
class DataCopyJDBC {
	def copy(Map<String,String> cfg, Sql srcSql, Sql dstSql, List<Map<String,Object>> tlist){
		int batchSize = cfg."data.copy.batch.size" as Integer
		tlist.each{table->
			println "copy ${table.name}..."
			long t = System.currentTimeMillis()
			Handler.beforeTableCopy(cfg, dstSql, table)
			//get first row to build insert statement
			List columns = ((Map)table.columns).collect{it.key}
			String sqlSelect = "select ${columns.join(',')} from ${table.name}"
			String sqlInsert = "insert into ${table.name} (${columns.join(',')}) values( ${columns.collect{'?'}.join(',')} )"

			//println sqlSelect
			//println sqlInsert
			//start batch
			long num = 0
			dstSql.withTransaction {
				dstSql.withBatch(batchSize, sqlInsert){BatchingPreparedStatementWrapper batch->
					srcSql.eachRow(sqlSelect){GroovyResultSet row->
						Collection values = row.toRowResult().values()
						batch.addBatch((List<Object>)values.collect())
						num++
						if(num%batchSize==0)println("    ${num} rows")
					}
				}
			}
			Handler.afterTableCopy(cfg, dstSql, table)
			println("    ${num} rows copied in ${(System.currentTimeMillis()-t)/1000} sec.")
		}
	}
}
