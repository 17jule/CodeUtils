import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlUtil {

	static SAXReader saxReader = new SAXReader();

	/**
	 * ���Xml �ĵ�����
	 * 
	 * @param xmlFile
	 *            ָ��xml �ļ�������
	 * @return xmlDoc ���ļ���ȡxml Document
	 */
	public static Document read(File xmlFile) {
		Document document = null;
		try {
			document = saxReader.read(xmlFile);
		} catch (DocumentException e) {
			System.out.println("ͨ��ָ��xml�ļ����ļ����Document����ʱ���� !");
		}
		return document;
	}

	/**
	 * ͨ��xml �ļ������ֶ�ȡDocument����
	 * 
	 * @param xmlFileName
	 * @return Document
	 */
	public static Document read(String xmlFileName) {
		return read(new File(xmlFileName));
	}

	/**
	 * ͨ��ָ��xml �ļ���URL���Document����
	 * 
	 * @param url
	 * @return Document
	 */
	public static Document read(URL url) {
		Document document = null;
		try {
			document = saxReader.read(url);
		} catch (DocumentException e) {
			System.out.println("ͨ��ָ��xml�ļ���URL���Document����ʱ����...");
		}
		return document;
	}
	
	static List<Element> elements = new ArrayList<Element>();
	public static void getChildElements(Element parentElement) {
		List<Element> elementList = parentElement.elements();
		for(Element element : elementList) {
			elements.add(element);
			if(element.elements().size() > 0) {
				getChildElements(element);
			}
		}
	}
	
	/**
	 * ��ȡxml�ļ���ȫ��element
	 * @param proPath
	 * @param xmlFileName
	 * @return
	 */
	public static List<Element> getAllElements(String proPath, String xmlFileName) {
		File file = FileUtils.getXmlFileByName(proPath, xmlFileName);
		Document doc = XmlUtil.read(file);
		Element rootElement = doc.getRootElement();
		
		getChildElements(rootElement);
		return elements;
	}
	
	/**
	 * ��ȡdocument��ȫ��element
	 * @param Document
	 * @return
	 */
	public static List<Element> getAllElements(Document doc) {
		Element rootElement = doc.getRootElement();
		
		elements = new ArrayList<Element>();
		getChildElements(rootElement);
		return elements;
	}
	
	
	/**
	 * �Ƿ����ĳ����ֵ
	 * @param element	��Ҫ��ѯ�Ľڵ�
	 * @param attrName	������(עandroid:������ǰ׺���ü�)
	 * @return
	 */
	public static boolean hasAttribute(Element element, String attrName) {
		List<Attribute> attributes = element.attributes();
		for(Attribute attr : attributes) {
			if(attrName.equals(attr.getName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ��Documentд����xml�ļ�
	 * @param file
	 * @param doc
	 */
	public static void write2xml(File file, Document doc) {
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileOutputStream(file));
			writer.write(doc);
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}