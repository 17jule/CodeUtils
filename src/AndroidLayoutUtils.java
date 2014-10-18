import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;


public class AndroidLayoutUtils {

	/**
	 * ���ò����ļ���id
	 * <p>
	 * ������ȫ��View��id,id���ָ�ʽΪ�����ļ���_�ؼ���_index
	 * 
	 * @param proPath		��Ŀ����·��
	 * @param layoutXml		�����ļ�����,��main.xml
	 */
	public static void setLayoutId(String proPath, String layoutXml) {
		String layoutName = layoutXml.substring(0, layoutXml.indexOf("."));
		
		File file = FileUtils.getXmlFileByName(proPath, layoutXml);
		Document doc = XmlUtil.read(file);
		List<Element> allElement = XmlUtil.getAllElements(doc);
		
		int index = 0;
		for (Element element : allElement) {
			if(!XmlUtil.hasAttribute(element, "id")) {
				String idValue = "@+id/" + layoutName 
						+ "_" + element.getName().toLowerCase(Locale.CHINESE) 
						+ "_" + (index++);
				element.addAttribute("android:id", idValue);
			}
		}

		XmlUtil.write2xml(file, doc);
	}
	
	/**
	 * �Զ�����xml�����д�id�Ŀؼ�,��activity�ļ������ö�Ӧ����,������Ϊid��
	 * @param proPath		��Ŀ����·��
	 * @param layoutXml		�����ļ�����,��main.xml
	 * @param activityFile	Activity���ļ���,��MainActivity.java
	 */
	public static void autoFindViewById(String proPath, String layoutXml, String activityFile) {
		File javaFile = FileUtils.getJavaFileByName(proPath, activityFile);
		List<Element> allElement = XmlUtil.getAllElements(proPath, layoutXml);
		
		List<IdNamingBean> idNamingBeans = new ArrayList<IdNamingBean>();
		for (Element element : allElement) {
			Attribute attribute = element.attribute("id");
			if(attribute != null) {
				String value = attribute.getValue();
				String idName = value.substring(value.indexOf("/") + 1);
				IdNamingBean bean = new IdNamingBean(element.getName(), idName);
				if(!idNamingBeans.contains(bean)) {
					idNamingBeans.add(bean);
				}
			}
		}
		
		String fileContent = FileUtils.readToString(javaFile);
		
		StringBuilder sb = new StringBuilder();
		for(IdNamingBean bean : idNamingBeans) {
			sb.append("private ")
				.append(bean.viewName)
				.append(" ")
				.append(bean.idName)
				.append(";")
				.append("\n");
		}
		
		sb.append("private void initView(){");
		
		for(IdNamingBean bean : idNamingBeans) {
			sb.append(bean.idName)
				.append(" = ")
				.append("(" + bean.viewName + ")")
				.append("findViewById(R.id." + bean.idName + ")")
				.append(";\n");
		}
		
		sb.append("}");
		
		fileContent = fileContent.replaceFirst("\\{", "\\{" + sb.toString());
		FileUtils.writeString2File(fileContent, javaFile);
	}
	
//	/**
//	 * ���ݲ����ļ��е�id�������ɶ�Ӧ�ı�����
//	 * <p>
//	 * ֻת���Զ����ɵ�id����,������_�ؼ���_xx,�Ὣ��ת�����շ���ʽ��"�ؼ���Xx"<br>
//	 * ��id=main_textview_name -> textviewName
//	 * @param layoutName	id�����ڵĲ����ļ���
//	 * @param idName		id����
//	 * @return
//	 */
//	private static String createFieldName(String layoutName, String idName) {
//		String fieldname = null;
//		//
//		return fieldname;
//	}
	
	
	/**
	 * ������ֵ��ȡ��values�ļ�����
	 * <p>��textColor=#ff00aa,��#ff00aa�����ľ���ֵ�滻Ϊ@color/colorname
	 * <br>����color.xml�ļ��ڴ���һ����Ӧ��ɫitem
	 * 
	 * @param proPath			��Ŀ����·��
	 * @param valuesXml			values�ļ�����,��strings.xml dimens.xml��
	 * @param type				��ȡ���ݵ�ǰ׺,��@color
	 * @param itemName			values�ļ���item������
	 * @param itemAttrName		values�ļ���item�Ĳ���,һ�㶼��name
	 * @param itemAttrValue		values�ļ���item�Ĳ���ֵ,Ҳ�ǳ�ȡֵ���滻������
	 * @param itemValue			values�ļ���item��ֵ
	 */
	public static void extract2values(String proPath, String valuesXml, String type,
			String itemName, String itemAttrName, String itemAttrValue, String itemValue) {
		try {
			FileUtils.getAllFiles(new File(proPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String valuesPath = proPath + "/res/values/" + valuesXml;
		File valuesFile = new File(valuesPath);
		if(!valuesFile.exists()) {
			System.out.println("�ļ�������,��ȷ���ļ�["+valuesXml+"]λ��/res/values/�ļ�����,���ļ�������ȷ");
			return;
		}
		
		int extractFileCount = 0;
		for(File file : FileUtils.xmlAllFiles) {
			if(file.getName().equals(valuesXml)) {
				continue;
			}
			
			String fileContent = FileUtils.readToString(file);
			if(!fileContent.contains(itemValue)) {
				continue;
			}
			
			fileContent = fileContent.replace(itemValue, "@"+type+"/"+itemAttrValue);
			FileUtils.writeString2File(fileContent, file);
			
			Document docValues = XmlUtil.read(valuesFile);
			Element rootElement = docValues.getRootElement();
			List<Element> elements = rootElement.elements();
			
			boolean hasColor = false;
			for(Element element : elements) {
				String color = element.attributeValue(itemAttrName);
				if(color.equals(itemAttrValue)) {
					hasColor = true;
					break;
				}
			}
			
			if(!hasColor) {
				Element element = rootElement.addElement(itemName);
				element.addAttribute(itemAttrName, itemAttrValue);
				element.setText(itemValue);
				
				XmlUtil.write2xml(valuesFile, docValues);
			}
			
			extractFileCount ++;
		}
		
		System.out.println("��[" + itemValue 
				+ "]��ȡΪ[" + valuesXml 
				+ "]�ļ��µ�[" + itemAttrValue + "]");
		System.out.println("����ȡ��" + extractFileCount + "���ļ��µ�����");
		System.out.println("-------------------------");
	}
	
	public static void extractColor(String proPath, String colorXml, String colorName, String RGB) {
		extract2values(proPath, colorXml, "color", "color", "name", colorName, RGB);
	}
}
