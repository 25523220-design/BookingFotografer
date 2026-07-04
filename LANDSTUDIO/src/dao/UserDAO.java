package dao;

import model.User;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class UserDAO {

    private final String FILE_NAME = "data/users.xml";

    // Membaca semua user
    public ArrayList<User> readUsers() {

        ArrayList<User> users = new ArrayList<>();

        try {

            File file = new File(FILE_NAME);

            if (!file.exists()) {
                return users;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(file);

            NodeList list = doc.getElementsByTagName("user");

            for (int i = 0; i < list.getLength(); i++) {

                Element e = (Element) list.item(i);

                int id = Integer.parseInt(e.getElementsByTagName("id").item(0).getTextContent());
                String nama = e.getElementsByTagName("nama").item(0).getTextContent();
                String email = e.getElementsByTagName("email").item(0).getTextContent();
                String password = e.getElementsByTagName("password").item(0).getTextContent();

                users.add(new User(id, nama, email, password));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;

    }

    // Simpan user baru
    public void save(User user) {

        try {

            File file = new File(FILE_NAME);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc;

            if (file.exists()) {

                doc = builder.parse(file);

            } else {

                doc = builder.newDocument();

                Element root = doc.createElement("users");
                doc.appendChild(root);

            }

            Element root = doc.getDocumentElement();

            Element userElement = doc.createElement("user");

            Element id = doc.createElement("id");
            id.appendChild(doc.createTextNode(String.valueOf(user.getId())));

            Element nama = doc.createElement("nama");
            nama.appendChild(doc.createTextNode(user.getNama()));

            Element email = doc.createElement("email");
            email.appendChild(doc.createTextNode(user.getEmail()));

            Element password = doc.createElement("password");
            password.appendChild(doc.createTextNode(user.getPassword()));

            userElement.appendChild(id);
            userElement.appendChild(nama);
            userElement.appendChild(email);
            userElement.appendChild(password);

            root.appendChild(userElement);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    // Login
    public User login(String email, String password) {

        ArrayList<User> users = readUsers();

        for (User u : users) {

            if (u.getEmail().equals(email)
                    && u.getPassword().equals(password)) {

                return u;

            }

        }

        return null;

    }

    // Cek email sudah ada
    public boolean emailSudahAda(String email) {

        ArrayList<User> users = readUsers();

        for (User u : users) {

            if (u.getEmail().equalsIgnoreCase(email)) {

                return true;

            }

        }

        return false;

    }

    // Generate ID
    public int getNextId() {

        ArrayList<User> users = readUsers();

        int max = 0;

        for (User u : users) {

            if (u.getId() > max) {

                max = u.getId();

            }

        }

        return max + 1;

    }

}