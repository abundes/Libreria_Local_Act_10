package com.example.act_final_libreria;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_CATEGORY_VISIBILITY = "categoryVisibility";
    private int currentCategory = 0;
    private boolean[] categoryVisibility = {false, false, false, false, false};
    private BookAdapter allBooksAdapter;
    private BookAdapter roboticsAdapter;
    private BookAdapter mechatronicsAdapter;
    private BookAdapter softwareDevAdapter;
    private BookAdapter programmingLanguagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Evitar que el teclado se abra automáticamente
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_main);

        // Inicializar y configurar la barra de búsqueda
        SearchView
                searchView = findViewById(R.id.searchView);


        // Deshabilitar la apertura automática del teclado al iniciar la actividad
        searchView.setFocusable(false);
        searchView.setFocusableInTouchMode(false);

        // Configurar el clic para abrir el teclado cuando haces clic en el SearchView
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Habilitar el enfoque y mostrar el teclado al hacer clic
                searchView.setFocusable(true);
                searchView.setFocusableInTouchMode(true);
                searchView.requestFocus();

                // Mostrar el teclado
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        // Encontrar el LinearLayout de los botones
        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);

        // Asignar un identificador
        buttonLayout.setId(R.id.buttonLayout);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        Button btnInicio = findViewById(R.id.btnInicio);
        Button btnRobotica = findViewById(R.id.btnRobotica);
        Button btnMecatronica = findViewById(R.id.btnMecatronica);
        Button btnSoftwareDev = findViewById(R.id.btnSoftwareDev);
        Button btnProgrammingLanguages = findViewById(R.id.btnProgrammingLanguages);

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategoryVisibility(0);
            }
        });

        btnRobotica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategoryVisibility(1);
            }
        });

        btnMecatronica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategoryVisibility(2);
            }
        });

        btnSoftwareDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategoryVisibility(3);
            }
        });

        btnProgrammingLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategoryVisibility(4);
            }
        });

        // Configuración inicial del RecyclerView para mostrar libros en la categoría de Robótica
        List<Book> allBooks = generateAllBooks();
        allBooksAdapter = new BookAdapter(allBooks);
        RecyclerView recyclerViewAllBooks = findViewById(R.id.Recycler_CardView_AllBooks);
        recyclerViewAllBooks.setLayoutManager(new GridLayoutManager(this, calculateSpanCount()));
        recyclerViewAllBooks.setAdapter(allBooksAdapter);

        // Categoría: Robótica
        List<Book> roboticsBooks = generateRoboticsBooks();
        roboticsAdapter = new BookAdapter(roboticsBooks);
        RecyclerView recyclerViewRobotics = findViewById(R.id.Recycler_CardView_Robotics);
        recyclerViewRobotics.setLayoutManager(new GridLayoutManager(this, calculateSpanCount()));
        recyclerViewRobotics.setAdapter(roboticsAdapter);

        // Categoría: Mecatrónica
        List<Book> mechatronicsBooks = generateMechatronicsBooks();
        mechatronicsAdapter = new BookAdapter(mechatronicsBooks);
        RecyclerView recyclerViewMechatronics = findViewById(R.id.Recycler_CardView_Mechatronics);
        recyclerViewMechatronics.setLayoutManager(new GridLayoutManager(this, calculateSpanCount()));
        recyclerViewMechatronics.setAdapter(mechatronicsAdapter);

        // Categoría: Desarrollo de Software
        List<Book> softwareDevBooks = generateSoftwareDevBooks();
        softwareDevAdapter = new BookAdapter(softwareDevBooks);
        RecyclerView recyclerViewSoftwareDev = findViewById(R.id.Recycler_CardView_SoftwareDev);
        recyclerViewSoftwareDev.setLayoutManager(new GridLayoutManager(this, calculateSpanCount()));
        recyclerViewSoftwareDev.setAdapter(softwareDevAdapter);

        // Categoría: Lenguajes de Programación
        List<Book> programmingLanguagesBooks = generateProgrammingLanguagesBooks();
        programmingLanguagesAdapter = new BookAdapter(programmingLanguagesBooks);
        RecyclerView recyclerViewProgrammingLanguages = findViewById(R.id.Recycler_CardView_ProgrammingLanguages);
        recyclerViewProgrammingLanguages.setLayoutManager(new GridLayoutManager(this, calculateSpanCount()));
        recyclerViewProgrammingLanguages.setAdapter(programmingLanguagesAdapter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(KEY_CATEGORY_VISIBILITY, categoryVisibility);
    }
    private void toggleCategoryVisibility(int categoryIndex) {
        if (currentCategory == categoryIndex) {
            categoryVisibility[categoryIndex] = !categoryVisibility[categoryIndex];
        } else {
            categoryVisibility[currentCategory] = false;
            categoryVisibility[categoryIndex] = true;
            currentCategory = categoryIndex;
        }

        updateRecyclerViewVisibility();
    }
    private void updateRecyclerViewVisibility() {
        RecyclerView recyclerViewAllBooks = findViewById(R.id.Recycler_CardView_AllBooks);
        RecyclerView recyclerViewRobotics = findViewById(R.id.Recycler_CardView_Robotics);
        RecyclerView recyclerViewMechatronics = findViewById(R.id.Recycler_CardView_Mechatronics);
        RecyclerView recyclerViewSoftwareDev = findViewById(R.id.Recycler_CardView_SoftwareDev);
        RecyclerView recyclerViewProgrammingLanguages = findViewById(R.id.Recycler_CardView_ProgrammingLanguages);

        recyclerViewAllBooks.setVisibility(View.VISIBLE);
        recyclerViewRobotics.setVisibility(View.VISIBLE);
        recyclerViewMechatronics.setVisibility(View.VISIBLE);
        recyclerViewSoftwareDev.setVisibility(View.VISIBLE);
        recyclerViewProgrammingLanguages.setVisibility(View.VISIBLE);

        recyclerViewAllBooks.setVisibility(categoryVisibility[0] ? View.VISIBLE : View.GONE);
        recyclerViewRobotics.setVisibility(categoryVisibility[1] ? View.VISIBLE : View.GONE);
        recyclerViewMechatronics.setVisibility(categoryVisibility[2] ? View.VISIBLE : View.GONE);
        recyclerViewSoftwareDev.setVisibility(categoryVisibility[3] ? View.VISIBLE : View.GONE);
        recyclerViewProgrammingLanguages.setVisibility(categoryVisibility[4] ? View.VISIBLE : View.GONE);
    }
    private int calculateSpanCount() {
        // Si la orientación es horizontal, devuelve 3, de lo contrario, devuelve 2
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
    }
    private List<Book> generateAllBooks() {
        List<Book> books = new ArrayList<>();
        books.addAll(generateRoboticsBooks());
        books.addAll(generateMechatronicsBooks());
        books.addAll(generateSoftwareDevBooks());
        books.addAll(generateProgrammingLanguagesBooks());
        return books;
    }
    private List<Book> generateRoboticsBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Mechanics of Robotic Manipulation",                      "0-262-13396-2",     "Matthew T. Mason",           R.drawable.robotica1, "0-262-13396-2.pdf"));
        books.add(new Book("Robótica Manipuladores y robots móviles",                "84-267-1313-0",     "Aníbal Ollero Batureno",     R.drawable.robotica2, "84-267-1313-0.pdf"));
        books.add(new Book("Introducción a la robótica",                             "978-0-07-066900-0", "Subir Kumar Saha",           R.drawable.robotica3, "978-0-07-066900-0.pdf"));
        books.add(new Book("ROBOT BUILDER’S BONANZA",                                "978-0-07-175035-6", "GORDON McCOMB",              R.drawable.robotica4, "978-0-07-175035-6.pdf"));
        books.add(new Book("El libro blanco de la robótica en España",               "978-84-615-4583-4", "J. Ramiro Martínez",         R.drawable.robotica5, "978-84-615-4583-4.pdf"));
        books.add(new Book("Probabilistic Robotics",                                 "978-0-262-20162-9", "Sebastian Thrun",            R.drawable.robotica6, "978-0-262-20162-9.pdf"));
        books.add(new Book("ROS Robotics By Example",                                "978-1-78847-959-2", "Carol Fairchild",            R.drawable.robotica7, "978-1-78847-959-2.pdf"));
        books.add(new Book("Learning Robotics using Python",                         "978-1-78862-331-5", "Lentin Joseph",              R.drawable.robotica8, "978-1-78862-331-5.pdf"));
        books.add(new Book("Robotics Vision and Control",                            "978-3-642-20143-1", "Peter Corke",                R.drawable.robotica9, "978-3-642-20143-1.pdf"));
        books.add(new Book("Trajectory Planning for Automatic Machines and Robots",  "978-3-540-85628-3", "Luigi Biagiotti",            R.drawable.robotica10, "978-3-540-85628-3.pdf"));

        return books;
    }
    private List<Book> generateMechatronicsBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Mechatronics Principles and Applications",                  "0-7506-6379-0",     "Godfrey C. Onwubolu",           R.drawable.meca1, "0-7506-6379-0.pdf"));
        books.add(new Book("MECATRÓNICA",                                               "978-0-273-74286-9", "William Bolton",                R.drawable.meca2, "978-0-273-74286-9.pdf"));
        books.add(new Book("Introduction to Mechatronics and Measurement Systems",      "978-0-07-338023-0", "David G. Alciatore",            R.drawable.meca3, "978-0-07-338023-0.pdf"));
        books.add(new Book("Mechatronics : An Integrated Approach",                     "0-8493-1274-4",     "De Silva, Clarence W",          R.drawable.meca4, "0-8493-1274-4.pdf"));
        books.add(new Book("Robótica y Mecatrónica",                                    "978-607-9394-14-1", "Ramos ArreguÌn Juan Manuel",    R.drawable.meca5, "978-607-9394-14-1.pdf"));

        return books;
    }
    private List<Book> generateSoftwareDevBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Clean Code",                                                         "0-13-235088-2",         "Robert C. Martin Series",                        R.drawable.desa1, "0-13-235088-2.pdf"));
        books.add(new Book("The Art of Readable Code",                                           "978-0-596-80229-5",     "Dustin Boswell and Trevor Foucher",              R.drawable.desa2, "978-0-596-80229-5.pdf"));
        books.add(new Book("Code Complete, Second Edition",                                      "0-7356-1967-0",         "Steve McConnell",                                R.drawable.desa3, "0-7356-1967-0.pdf"));
        books.add(new Book("Domain-Driven Design: Tackling Complexity in the Heart of Software", "0-321-12521-5",         " Eric Evans",                                    R.drawable.desa4, "0-321-12521-5.pdf"));
        books.add(new Book("Ingeniería de software en Google",                                   "978-1-492-08279-8",     "Titus Winters, Tom Manshreck y Hyrum Wright",    R.drawable.desa5, "978-1-492-08279-8.pdf"));

        return books;
    }
    private List<Book> generateProgrammingLanguagesBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("The Pragmatic Programmer",      "0-201-61622-X",      "Andrew Hunt, David Thomas", R.drawable.leng1, "0-201-61622-X.pdf"));
        books.add(new Book("Effective Java Third Edition",  "978-0-13-468599-1",  "Joshua Bloch", R.drawable.leng2, "978-0-13-468599-1.pdf"));
        books.add(new Book("Python Crash Course",           "1-59327-603-6",      "Eric Matthes", R.drawable.leng3, "1-59327-603-6.pdf"));
        books.add(new Book("JavaScript: The Good Parts",    "978-0-596-51774-8",  "Douglas Crockford", R.drawable.leng4, "978-0-596-51774-8.pdf"));
        books.add(new Book("Eloquent JavaScript",           "1593279507", "Marijn Haverbeke", R.drawable.leng5, "1593279507.pdf"));
        books.add(new Book("Programming in C",              "0-672-32666-3", "Stephen G. Kochan", R.drawable.leng6, "0-672-32666-3.pdf"));
        books.add(new Book("C# in Depth",                   "9781617294532", "JON SKEET", R.drawable.leng7, "9781617294532.pdf"));
        books.add(new Book("Ruby for Rails",                "1932394699", "DAVID A. BLACK", R.drawable.leng8, "1932394699.pdf"));
        books.add(new Book("Effective Python",              "0-13-485398-9", "Brett Slatkin", R.drawable.leng9, "0-13-485398-9.pdf"));
        books.add(new Book("Head First Java",               "5960092085", "Kathy Sierra", R.drawable.leng10, "5960092085.pdf"));
        return books;
    }
    private void filterBooks(String query) {
        allBooksAdapter.filter(query);
        roboticsAdapter.filter(query);
        mechatronicsAdapter.filter(query);
        softwareDevAdapter.filter(query);
        programmingLanguagesAdapter.filter(query);
    }
    public static class Book {
        private String title;
        private String ISBN;
        private String author;
        private int imageResource;
        private String pdfPath;

        public Book(String title, String ISBN, String author, int imageResource, String pdfPath) {
            this.title = title;
            this.ISBN = ISBN;
            this.author = author;
            this.imageResource = imageResource;
            this.pdfPath = pdfPath;
        }

        public String getTitle() {
            return title;
        }

        public String getISBN() {
            return ISBN;
        }

        public String getAuthor() {
            return author;
        }

        public int getImageResource() {
            return imageResource;
        }

        public String getPdfPath() {
            return pdfPath;
        }
    }
    public static class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
        private final List<Book> books;
        private final List<Book> booksFiltered;

        public BookAdapter(List<Book> books) {
            this.books = books;
            this.booksFiltered = new ArrayList<>(books);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarjeta, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Book book = booksFiltered.get(position);
            holder.imageViewBook.setImageResource(book.getImageResource());
            holder.textViewBookTitle.setText(book.getTitle());
            holder.textViewBookISBN.setText("ISBN: " + book.getISBN());
            holder.textViewBookAuthor.setText("Autor: " + book.getAuthor());

            // Agregar un clic al CardView
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Abrir la actividad SimplePDFViewActivity y pasar la ruta del PDF como extra
                    Intent intent = new Intent(v.getContext(), SimplePDFView.class);
                    intent.putExtra(SimplePDFView.EXTRA_PDF_PATH, book.getPdfPath());
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return booksFiltered.size();
        }

        public void filter(String query) {
            booksFiltered.clear();
            if (query.isEmpty()) {
                booksFiltered.addAll(books);
            } else {
                query = query.toLowerCase();
                for (Book book : books) {
                    if (book.getTitle().toLowerCase().contains(query) ||
                            book.getISBN().toLowerCase().contains(query) ||
                            book.getAuthor().toLowerCase().contains(query)) {
                        booksFiltered.add(book);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageViewBook;
            TextView textViewBookTitle;
            TextView textViewBookISBN;
            TextView textViewBookAuthor;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageViewBook = itemView.findViewById(R.id.imageViewBook);
                textViewBookTitle = itemView.findViewById(R.id.textViewBookTitle);
                textViewBookISBN = itemView.findViewById(R.id.textViewBookISBN);
                textViewBookAuthor = itemView.findViewById(R.id.textViewBookAuthor);
            }
        }
    }
}


