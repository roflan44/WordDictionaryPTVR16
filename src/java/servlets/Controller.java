/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entity.History;
import entity.Student;
import entity.User;
import entity.UserWords;
import entity.Word;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static jdk.nashorn.internal.objects.NativeMath.random;
import role.RoleLogic;
import role.Roles;
import session.HistoryFacade;
import session.RoleFacade;
import session.StudentFacade;
import session.UserFacade;
import session.UserRolesFacade;
import session.UserWordsFacade;
import session.WordFacade;
import utils.DateUtils;
import utils.Encription;
import utils.PagePathLoader;
import utils.SortUtils;

/**
 *
 * @author pupil
 */
@WebServlet(name = "Controller", urlPatterns = {
    "/showNewWord",
    "/addNewWord",
    "/showListWords",
    "/showChangePassword",
    "/changePassword",
    "/showEditWord",
    "/editWord",
    "/showDeleteWord",
    "/deleteWord",
    "/showLearnedWords",
    "/hideWord",
    "/returnWord",
    "/showLearning",
    "/manageWords",
    "/showCheckingWords",
    "/checkWord",
    "/showPageStatistic",
    "/showStatistic"
    

})
public class Controller extends HttpServlet {

    @EJB
    private UserFacade userFacade;
    @EJB
    private RoleFacade roleFacade;
    @EJB
    private StudentFacade studentFacade;
    @EJB
    private UserRolesFacade userRolesFacade;
    @EJB
    private WordFacade wordFacade;
    @EJB
    private HistoryFacade historyFacade;
    @EJB
    private UserWordsFacade userWordsFacade;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        RoleLogic roleLogic = new RoleLogic();
        HttpSession session = request.getSession(false);
        User regUser = (User) session.getAttribute("regUser");
        if (session == null) {
            request.setAttribute("info", "Войдите");
            request.getRequestDispatcher("/showLogin");

        }

        if (regUser == null) {

            request.setAttribute("info", "Войдите");
            request.getRequestDispatcher("/showLogin");

        }

        Boolean isRole = roleLogic.isRole(Roles.USER.toString(), regUser);
        if (!isRole) {
            request.setAttribute("info", "У вас должна быть роль USER");
            request.getRequestDispatcher("/showLogin").forward(request, response);;
        }
        Calendar c = new GregorianCalendar();
        Encription encription = new Encription();
        String path = request.getServletPath();
        if (path != null) {
            switch (path) {

                case "/showNewWord":

                    request.getRequestDispatcher(PagePathLoader.getPagePath("showNewWord")).forward(request, response);

                    break;

                case "/addNewWord":
                    String wordName = request.getParameter("word");
                    String translation = request.getParameter("translation");
                    String phrase = request.getParameter("phrase");
                    Word word = new Word(wordName, translation, phrase);
                    wordFacade.create(word);
                    UserWords userWords = new UserWords(regUser, word);
                    userWordsFacade.create(userWords);
                    request.setAttribute("info", " Слово: \"" + word.getWord() + "\" добавлено ");
                    request.getRequestDispatcher("/showListWords").forward(request, response);
                    break;

                case "/showListWords":
                    List<UserWords> listUserWords = userWordsFacade.findNoHidden(regUser);
                    if (!listUserWords.isEmpty()) {
                        request.setAttribute("listUserWords", listUserWords);
                        request.getRequestDispatcher(PagePathLoader.getPagePath("showListWords")).forward(request, response);
                    } else {
                        request.setAttribute("info", "Список слов пуст");
                        request.getRequestDispatcher(PagePathLoader.getPagePath("showListWords")).forward(request, response);
                    }

                    break;
                case "/showLearning":
                     List<UserWords> listUserWordsAll = userWordsFacade.findNoHidden(regUser);
                     listUserWords = null;
//                    List<Word> listWordsAll = wordFacade.findNoHidden();
//                    List<Word> listWords = null;
                    if (!listUserWordsAll.isEmpty()) {
                        listUserWords = (List<UserWords>) session.getAttribute("listUserWords");
                        listUserWords = userWordsFacade.findNoHidden(regUser);
                        Random rand = new Random();
                       UserWords userWord = listUserWords.get(rand.nextInt(listUserWords.size())); // Math.random(listWordsss.size());
                        request.setAttribute("UserWords", userWord);
                        listUserWords.remove(userWord);

                    }
                    if (listUserWordsAll.isEmpty() || listUserWords.size()==0) {
                        request.setAttribute("info", "Список слов пуст");
                        request.getRequestDispatcher(PagePathLoader.getPagePath("showLearning")).forward(request, response);
                    }

                    session.setAttribute("UserWords", listUserWords);
                    request.getRequestDispatcher(PagePathLoader.getPagePath("showLearning")).forward(request, response);

                    break;
                case "/showLearnedWords":
                    listUserWords = userWordsFacade.findHidden(regUser);
                    request.setAttribute("listUserWords", listUserWords);
                    request.getRequestDispatcher(PagePathLoader.getPagePath("showLearnedWords")).forward(request, response);
                    break;
                case "/hideWord":

                    String wordId = request.getParameter("id");
                    Word getWord = wordFacade.find(new Long(wordId));
                    
                    getWord.setHidden(true);
                    wordFacade.edit(getWord);
                    
                    request.setAttribute("info", "Слово \"" + getWord.getWord() + "\" добавлено в выученные!");
                    request.getRequestDispatcher("/showListWords").forward(request, response);
                    break;
                case "/returnWord":

                    wordId = request.getParameter("id");
                    getWord = wordFacade.find(new Long(wordId));
                    getWord.setHidden(false);
                    wordFacade.edit(getWord);
                    request.setAttribute("info", "Слово \"" + getWord.getWord() + "\" добавлено обратно в словарь!");
                    request.getRequestDispatcher("/showListWords").forward(request, response);
                    break;
                case "/showChangePassword":

                    String username = regUser.getStudent().getName() + " " + regUser.getStudent().getSurname();
                    request.setAttribute("username", username);
                    String login = regUser.getLogin();
                    request.setAttribute("login", login);
                    request.getRequestDispatcher(PagePathLoader.getPagePath("changePassword")).forward(request, response);

                    break;
                case "/changePassword":

                    String oldPassword = request.getParameter("oldPassword");

                    String encriptOldPassword = encription.getEncriptionPass(oldPassword);
                    if (!encriptOldPassword.equals(regUser.getPassword())) {
                        request.setAttribute("info", "Вы должны войти");
                        request.getRequestDispatcher("/showLogin").forward(request, response);
                        break;
                    }
                    String newPassword1 = request.getParameter("newPassword1");
                    String newPassword2 = request.getParameter("newPassword2");
                    if (newPassword1.equals(newPassword2)) {
                        regUser.setPassword(encription.getEncriptionPass(newPassword1));
                        userFacade.edit(regUser);
                    }
                    request.setAttribute("info", "Вы успешно изменили пароль");
                    request.getRequestDispatcher("/logout");
                    request.getRequestDispatcher("/showLogin").forward(request, response);
                    break;
                case "/showEditWord":
                    wordId = request.getParameter("id");
                    getWord = wordFacade.find(new Long(wordId));
                    request.setAttribute("word", getWord);

                    request.getRequestDispatcher(PagePathLoader.getPagePath("showEditWord")).forward(request, response);
                    break;
                case "/editWord":

                    String wordText = request.getParameter("word");
                    String wordTranslation = request.getParameter("translation");
                    String wordPhrase = request.getParameter("phrase");
                    wordId = request.getParameter("id");
                    getWord = wordFacade.find(new Long(wordId));
                    getWord.setWord(wordText);
                    getWord.setTranslation(wordTranslation);
                    getWord.setPhrase(wordPhrase);
                    wordFacade.edit(getWord);
                    request.setAttribute("info", "Слово \"" + getWord.getWord() + "\" изменено!");
                    request.getRequestDispatcher("/showListWords").forward(request, response);
                    break;
                case "/deleteWord":

                    wordId = request.getParameter("id");
                    try {
                        getWord = wordFacade.find(new Long(wordId));

                        word = wordFacade.find(new Long(wordId));
                        word.setDeleted(true);
                        wordFacade.edit(word);
                    } catch (Exception e) {
                        request.setAttribute("info", "Слово не удалось удалить");
                        request.getRequestDispatcher("/showListWords").forward(request, response);
                        break;
                    }

                    request.setAttribute("info", "Слово \"" + getWord.getWord() + "\" удалено!");
                    request.getRequestDispatcher("/showListWords").forward(request, response);

                    break;
                case "/manageWords":
                    word = null;
                    String[] words = request.getParameterValues("words");
                    String deleteWords = request.getParameter("deleteWords");
                    String hideWords = request.getParameter("hideWords");
                    String returnWords = request.getParameter("returnWords");
                    try {
                        if (deleteWords != null) {
                            for (int i = 0; i < words.length; i++) {
                                word = wordFacade.find(new Long(words[i]));
                                word.setDeleted(true);
                                wordFacade.edit(word);
                            }
                            request.setAttribute("info", "Слова удалены.");
                            request.getRequestDispatcher("/showListWords").forward(request, response);
                        }

                    } catch (Exception e) {
                        request.setAttribute("info", "Слова не удалось удалить.");
                        request.getRequestDispatcher("/showListWords").forward(request, response);
                    }
                    try {
                        if (hideWords != null) {
                            for (int i = 0; i < words.length; i++) {
                                word = wordFacade.find(new Long(words[i]));                               
                                word.setHidden(true);
                                wordFacade.edit(word);                               
                            }
                            request.setAttribute("info", "Слова добавлены в выученные.");
                            request.getRequestDispatcher("/showListWords").forward(request, response);
                        }

                    } catch (Exception e) {
                        request.setAttribute("info", "Слова не удалось добавить.");
                        request.getRequestDispatcher("/showListWords").forward(request, response);
                    }

                    try {
                        if (returnWords != null) {
                            for (int i = 0; i < words.length; i++) {
                                word = wordFacade.find(new Long(words[i]));
                                
                                word.setHidden(false);
                                wordFacade.edit(word);
                            }
                            request.setAttribute("info", "Слова добавлены обратно в словарь.");
                            request.getRequestDispatcher("/showListWords").forward(request, response);
                        }

                    } catch (Exception e) {
                        request.setAttribute("info", "Слова не удалось добавить обратно в словарь.");
                        request.getRequestDispatcher("/showListWords").forward(request, response);
                    }
                case "/showCheckingWords":
                    List<UserWords> CheckListWordsAll = userWordsFacade.findNoHidden(regUser);
                    List<UserWords> listWords = null;
                    if (!CheckListWordsAll.isEmpty()) {
                        listWords = (List<UserWords>) session.getAttribute("listUserWords");
                        listWords = userWordsFacade.findNoHidden(regUser);
                        Random rand = new Random();
                       UserWords userWord = listWords.get(rand.nextInt(listWords.size())); // Math.random(listWordsss.size());
                        request.setAttribute("userWords", userWord);
                        listWords.remove(userWord);
                        request.getRequestDispatcher(PagePathLoader.getPagePath("showCheckingWords")).forward(request, response);

                    }
                    if (CheckListWordsAll.isEmpty() || CheckListWordsAll.size()==0) {
                        request.setAttribute("info", "Список слов пуст");
                        request.getRequestDispatcher(PagePathLoader.getPagePath("showCheckingWords")).forward(request, response);
                    }
                    break;
                case "/checkWord":
                    wordId = request.getParameter("wordId");
                    word = wordFacade.find(new Long(wordId));
                    Long studentId = regUser.getStudent().getId();
                    Student getStudent = studentFacade.find(new Long(studentId));
                  List<History> listHistories = historyFacade.findAll();
                 History history = historyFacade.findByWord(word);
                    if (!listHistories.contains(history)) {
                         history = new History(word,getStudent, c.getTime(),0,0);
                    
                        historyFacade.create(history);
                    }
                    
                    String answer = request.getParameter("answer");
                    //String wordCheckTranslation = request.getParameter("check");

                    if (answer.equals(word.getTranslation())) {
                        
                        history = historyFacade.findByWord(word);
                        history.setRating(history.getRating()+1);
                        history.setCount(history.getCount()+1);
                        historyFacade.edit(history);
                        
                        
                        request.setAttribute("info", "Вы ответили правильно!");
                    } else {
                        history = historyFacade.findByWord(word);
                        history.setRating(history.getRating()-1);
                        history.setCount(history.getCount()+1);
                        historyFacade.edit(history);
                        request.setAttribute("info", "Вы ответили неправильно. Ответ: " + word.getTranslation());
                        
                    }
                    request.setAttribute("word", word);
                    
                    
                    request.getRequestDispatcher("/showCheckingWords").forward(request, response);

                        
                      break;
                case"/showPageStatistic":
                
                request.getRequestDispatcher(PagePathLoader.getPagePath("showPageStatistic")).forward(request, response);
                    break;
            case "/showStatistic":
                   
                     
                String popWords = request.getParameter("popWords");
                
                if(popWords != null){
                    listHistories = historyFacade.findAll();
                   List<Word> listPopularWords = new ArrayList<>();
                    request.setAttribute("listHistories", listHistories);
                    Map<Word,Integer> mapWordsRate = new HashMap<>();
                    for (int i = 0; i < listHistories.size(); i++) {
                        history = listHistories.get(i);
                        if(!listPopularWords.contains(history.getWord())){
                            listPopularWords.add(history.getWord());
                            mapWordsRate.put(history.getWord(), 1);
                        }else{
                            mapWordsRate.merge(history.getWord(), 1, Integer::sum);
                        }
                    }
                   
                    Map<Word,Integer> sortedMapWordsRate = SortUtils.sortMapReverseByValue(mapWordsRate);
                    request.setAttribute("sortedMapWordsRate", sortedMapWordsRate);
                }
                    request.getRequestDispatcher(PagePathLoader.getPagePath("showPageStatistic")).forward(request, response);                    
                break;

//                default:
//                    request.setAttribute("info", "Нет тако");
//                    request.getRequestDispatcher(PagePathLoader.getPagePath("error")).forward(request, response);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
