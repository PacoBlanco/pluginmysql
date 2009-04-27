package es.itecban.deployment.environment.tasks.database;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;

import es.itecban.deployment.environment.runner.DeploymentActivityTranslator;
import es.itecban.deployment.environment.runner.ExecutableActivity;
import es.itecban.deployment.environment.runner.ant.AntExecActivity;
import es.itecban.deployment.environment.runner.tasks.database.Activator;
import es.itecban.deployment.model.deployment.plan.DeploymentActivityType;
import es.itecban.deployment.model.deployment.target.ContainerType;
import es.itecban.deployment.model.deployment.target.ContainerTypeType;
import es.itecban.deployment.model.deployment.unit.DeploymentUnitType;
import es.itecban.deployment.model.deployment.unit.PropertyType;
import es.itecban.deployment.model.deployment.unit.ResourceType;

public class Translator implements DeploymentActivityTranslator {

	public ExecutableActivity translate(DeploymentActivityType activity, ContainerType container,
			DeploymentUnitType du, Properties props) {
		AntExecActivity act = new AntExecActivity();
		String filepath;
		try {
			filepath = FileLocator.toFileURL(FileLocator.find(Activator.getPlugin().ctx
					.getBundle(), new Path("/ant/build.xml"), null)).getPath();
			int last = filepath.lastIndexOf('/');
			String basedir = filepath.substring(0, last);
			System.out.println(basedir);
			
			// Get database type
			EList<ContainerTypeType> containerTypes = container.getContainerTypes().getContainerType();
			ContainerTypeType conType = null;
			for (ContainerTypeType containerType : containerTypes) {
				if (containerType.getName().equals("es.itecban.deployment.container.db.oracle")
				 || containerType.getName().equals("es.itecban.deployment.container.db.mysql")) {
					conType = containerType;
					break;
				}
			}
			String dbType = conType.getName();
			
			// Get hostname and port
			EList<PropertyType> containerProperties = container.getContainerProperties().getContainerProperty();
			PropertyType hostnameProperty = null;
			PropertyType portProperty = null;
			for (PropertyType containerProperty : containerProperties) {
				if (containerProperty.getName().equals("hostName")) hostnameProperty = containerProperty;
				if (containerProperty.getName().equals("dbPort")) portProperty = containerProperty;
			}
			String hostname = hostnameProperty.getValue();
			String port = portProperty.getValue();			
			String dataLanguage = "fakeDL";
			
			List<ResourceType> exportedResources = du.getExportedResources().getExportedResource();
			ResourceType typeResource = null;
			for (ResourceType exportedResource : exportedResources) {
				List<String> types = exportedResource.getTypes().getType();
				for (String type : types) {
					if (type.equals("es.itecban.deployment.db.ddl")) {
						typeResource = exportedResource;
						dataLanguage = "_DDL";
					}
					if (type.equals("es.itecban.deployment.db.dml")) {
						typeResource = exportedResource;
						dataLanguage = "_DML";
					}
				}
			}
			List<PropertyType> exportedResourceProperties = typeResource.getResourceProperties().getResourceProperty();
			PropertyType dbNameProperty = null;
			PropertyType schemaNameProperty = null;
			for (PropertyType resourceProperty : exportedResourceProperties) {
				if (resourceProperty.getName().equals("es.itecban.deployment.db.name")) dbNameProperty = resourceProperty;
				if (resourceProperty.getName().equals("es.itecban.deployment.db.schema.name")) schemaNameProperty = resourceProperty;
			}
			String dbName = dbNameProperty.getValue();
			String schemaName = schemaNameProperty.getValue();
			
			// Define some properties according to database type
			String driver = "fakeDriver";
			String dataType = "fakeDataType";
			String databaseType = "fakeDatabaseType";
			String filename = "fakeFilename";
			String url = "fakeURL";
			if (dbType.equals("es.itecban.deployment.container.db.oracle")) {
				driver = "oracle.jdbc.driver.OracleDriver";
				dataType = "org.dbunit.ext.oracle.Oracle10DataTypeFactory";
				databaseType = "oracle";
				filename= "Oracle10g";
				url= "jdbc:oracle:thin:@" + hostname + ":" + port + ":" + dbName; 
			}
			if (dbType.equals("es.itecban.deployment.container.db.mysql")) {
				driver = "com.mysql.jdbc.Driver";
				dataType = "org.dbunit.ext.mysql.MySqlDataTypeFactory";
				databaseType = "mysql";
				filename="mysql";
				url = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName;
			}
			
			// Set properties
			props.put("es.itecban.deployment.unit.version", du.getVersion());
			props.put("es.itecban.deployment.container.url", url);
			props.put("es.itecban.deployment.db.schema.name", schemaName);
			props.put("database.driver", driver);
			props.put("database.datatype", dataType);
			props.put("precondition.db", databaseType);
			props.put("filename.db", filename);
			
			//Example: INSTALL_DEPLOYMENT_UNIT + _DDL|_DML 
			String target = activity.getType().getLiteral() + dataLanguage;
	
			//act.setTarget(activity.getType().getLiteral());
			act.setTarget(target);
			act.setBuildFilePath(filepath);
			act.setBaseDir(basedir);
			act.setReqProperties(props);
			return act;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}