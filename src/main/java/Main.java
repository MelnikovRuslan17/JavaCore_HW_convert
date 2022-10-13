import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String fileCSV = "data.csv";
        String fileXML = "data.xml";

        List<Employee> list = parseCSV(columnMapping, fileCSV);
        String json = listToJson(list);
        writeString(json, "data.json");

        List<Employee> list1 = parseXML(fileXML);
        String json1 = listToJson(list1);
        writeString(json1, "data1.json");
    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileCSV) {

        try (CSVReader reader = new CSVReader(new FileReader(fileCSV))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();

            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Employee> parseXML(String fileXML) {
        List<Employee> list2 = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fileXML);

            Node root = doc.getDocumentElement();
            list2 = read(root);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return list2;
    }

    public static List<Employee> read(Node node) {
        List<Employee> list = new ArrayList<>();
        String[] arr;
        long id;
        String firstName;
        String lastName;
        String country;
        int age;
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType() && node_.getNodeName().equals("employee")) {
                Element element = (Element) node_;
                arr = element.getTextContent().trim().replaceAll("\\s+", ",").split(",");
                id = Long.parseLong(arr[0]);
                firstName = arr[1];
                lastName = arr[2];
                country = arr[3];
                age = Integer.parseInt(arr[4]);

                Employee em= new Employee(id, firstName, lastName, country, age);
                list.add(em);
                read(node_);
            }
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);

        return json;
    }

    public static boolean writeString(String json, String fileName) {
        boolean flag = false;
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
            fileWriter.flush();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }
}
