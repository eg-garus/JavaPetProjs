import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AverageCorrector {

    private static final double EPSILON = 0.005;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Использование: java AverageCorrector <входной файл> <выходной файл>");
            return;
        }

        String inputFileName = args[0];
        String outputFileName = args[1];

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(inputFileName));
            Element student = document.getDocumentElement();
            System.out.println("Студент: " + student.getAttribute("lastname"));

            NodeList subjects = student.getElementsByTagName("subject");
            if (subjects.getLength() == 0) {
                System.out.println("В документе нет ни одного предмета - коррекция невозможна.");
                return;
            }

            int sum = 0;
            StringBuilder marksInfo = new StringBuilder();
            for (int i = 0; i < subjects.getLength(); i++) {
                Element subject = (Element) subjects.item(i);
                int mark = Integer.parseInt(subject.getAttribute("mark"));
                sum += mark;
                if (i > 0) {
                    marksInfo.append(", ");
                }
                marksInfo.append(subject.getAttribute("title")).append(" - ").append(mark);
            }

            double realAverage = round2((double) sum / subjects.getLength());

            System.out.println("Оценки: " + marksInfo);
            System.out.printf("Правильная средняя оценка: %.2f%n", realAverage);

            NodeList averageList = student.getElementsByTagName("average");
            Element average;
            if (averageList.getLength() == 0) {
                average = document.createElement("average");
                student.appendChild(average);
                System.out.println("Элемент average отсутствовал - он добавлен в документ.");
            } else {
                average = (Element) averageList.item(0);
            }

            String storedText = average.getTextContent().trim();
            double storedValue = 0;
            boolean storedIsNumber = true;
            try {
                storedValue = Double.parseDouble(storedText);
            } catch (NumberFormatException e) {
                storedIsNumber = false;
            }

            if (!storedIsNumber || Math.abs(storedValue - realAverage) >= EPSILON) {
                System.out.printf("Записанное значение (\"%s\") неверно - исправлено на %.2f%n",
                        storedText, realAverage);
                average.setTextContent(String.valueOf(realAverage));
            } else {
                System.out.printf("Записанное значение (%.2f) верно - документ не изменен.%n",
                        storedValue);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "student.dtd");
            transformer.transform(new DOMSource(document), new StreamResult(new File(outputFileName)));

            System.out.println("Результат записан в файл: " + outputFileName);

        } catch (Exception e) {
            System.out.println("Ошибка обработки документа: " + e.getMessage());
        }
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
