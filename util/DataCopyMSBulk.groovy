/*   */
import groovy.sql.Sql;
import groovy.sql.GroovyResultSet;
import java.sql.ResultSet;
import groovy.sql.BatchingPreparedStatementWrapper;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;

class DataCopyMSBulk {
	def copy(Map<String,String> cfg, Sql srcSql, Sql dstSql, List<Map<String,Object>> tlist){
		int batchSize = cfg."data.copy.batch.size" as Integer
		tlist.each{table->
			println "copy ${table.name}..."
			long t = System.currentTimeMillis()
			Handler.beforeTableCopy(cfg, dstSql, table)
			//get first row to build insert statement
			List columns = ((Map)table.columns).collect{it.key}
			String sqlSelect = "select ${columns.join(',')} from ${table.name}"
			//String sqlInsert = "insert into ${table.name} (${columns.join(',')}) values( ${columns.collect{'?'}.join(',')} )"
			//start batch
			SQLServerBulkCopy bulkCopy = new SQLServerBulkCopy(dstSql.getConnection())
			bulkCopy.getBulkCopyOptions().setBatchSize(batchSize)
			bulkCopy.getBulkCopyOptions().setKeepIdentity(true)
			bulkCopy.setDestinationTableName(table.name)
			srcSql.query(sqlSelect){ResultSet rs->
				bulkCopy.writeToServer(rs)
			}
			bulkCopy.close()

			//println sqlSelect
			Handler.afterTableCopy(cfg, dstSql, table)
			def num = -1;
			println("    ${num} rows copied in ${(System.currentTimeMillis()-t)/1000} sec.")
		}
	}
}
