package me.ryguy.ctfbot.util;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//Shoutout to redslimes wafflebot for teaching me 90% of this <3
public class SSHelper {

    public static Sheets SHEETS = GoogleSheets.getSheets();

    //handles caching and grabbing spreadsheet information
    private long lastCache;
    private ValueRange vr;
    //handles ss specific variables (i should probably make this a class applicable to any spreadsheet though)
    public static final int ROW_DATE = 0;
    public static final int ROW_DAY = 1;
    public static final int COLUMN_TIME = 0;
    public static final int MAX_ROW = 49;
    public static final int MAX_COLUMN = 18;
    private String SHEET_ID;
    private String RANGE;

    public SSHelper(String id, String range) throws IOException {
        this.SHEET_ID = id;
        this.RANGE = range;
        this.vr = getValueRange();
    }

    public ValueRange getValueRange() throws IOException {
        if(lastCache == 0 || System.currentTimeMillis() - lastCache > TimeUnit.MINUTES.toMillis(1)) {
            vr = SHEETS.spreadsheets().values().get(SHEET_ID, RANGE).execute();
            lastCache = System.currentTimeMillis();
        }
        return vr;
    }
    //redslimes cell getters <3
    public List<Cell> getAllCells() throws IOException {
        List<Cell> cells = new ArrayList<>();
        List<List<Object>> data = getValueRange().getValues();
        for(int rowIndex = 0; rowIndex < MAX_ROW; rowIndex++) {
            for(int columnIndex = 0; columnIndex < MAX_COLUMN; columnIndex++) {
                if(data.size() > rowIndex) {
                    if(data.get(rowIndex).size() > columnIndex) {
                        String str = (String) data.get(rowIndex).get(columnIndex);
                        cells.add(new Cell(rowIndex, columnIndex, str));
                    } else
                        cells.add(new Cell(rowIndex, columnIndex, ""));
                } else
                    cells.add(new Cell(rowIndex, columnIndex, ""));
            }
        }
        return cells;
    }

    public List<Cell> getRow(int index) throws IOException {
        return getAllCells().stream().filter(c -> c.row == index).collect(Collectors.toList());
    }

    public List<Cell> getColumn(int index) throws IOException {
        return getAllCells().stream().filter(c -> c.column == index).collect(Collectors.toList());
    }

    public Cell getCell(int row, int column) throws IOException {
        return getAllCells().stream().filter(c -> c.row == row && c.column == column).findFirst().orElse(null);
    }
    public List<Cell> getInnerCells() throws IOException {
        return getAllCells().stream().filter(c -> c.row != ROW_DATE && c.row != ROW_DAY && c.column != COLUMN_TIME).collect(Collectors.toList());
    }
    public List<Match> getMatches() throws IOException, ParseException {
        List<Match> matches = new ArrayList<>();
        for(Cell cell : getInnerCells()) {
            if(cell.value == null || cell.value.trim().equals("") || cell.value.trim().equals("^"))
                continue;

            Cell end = cell.getRelative(Util.Direction.DOWN);
            while (end != null && end.value != null && end.value.trim().equals("^")) {
                if(end.getRelative(Util.Direction.DOWN) == null)
                    break;
                end = end.getRelative(Util.Direction.DOWN);
            }
            if(end != null) {
                matches.add(new Match(cell, cell.value, cell.getTimeEST(), end.getTimeEST()));
            }
        }
        matches.sort((o1, o2) -> {
            if(o1.begin.toInstant().isBefore(o2.begin.toInstant())) return -1;
            if(o1.begin.toInstant().isAfter(o2.begin.toInstant())) return 1;
            return 0;
        });
        return matches;
    }
    //we love redslime
    @Getter @Setter
    public class Match {
        Cell parent;
        String name;
        public Date begin;
        public Date end;

        public Match(Cell parent, String name, String begin, String end) throws IOException, ParseException {
            this.parent = parent;
            this.name = name;

            // input= M/D/YYYY h:mma <=> 4/7/2018 4:30pm
            for(int i = 0; i < 2; i++) {
                String input = getCell(ROW_DATE, parent.column).getValue() + " " + (i == 0 ? begin.replace("pm", "PM").replace("am", "AM") : end.replace("pm", "PM").replace("am", "AM"));
                SimpleDateFormat parser = new SimpleDateFormat("M/d/yyyy h:mma");
                parser.setTimeZone(TimeZone.getTimeZone("EST"));
                if(i == 0) this.begin = parser.parse(input);
                else this.end = parser.parse(input);
            }
        }
        public String getDay() throws IOException{
            return getCell(ROW_DAY, parent.getColumn()).getValue();
        }
        @Override
        public String toString() {
            return "Match[parent=" + parent.toString() + ", name=" + name + ", begin=" + begin + ", end= " + end + "]";
        }
    }
    @Getter @Setter
    public class Cell {
        int row;
        int column;
        String value;
        Cell(int row, int column, String string) {
            this.row = row;
            this.column = column;
            this.value = string;
        }
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Cell) {
                Cell c = (Cell) obj;
                return c.row == row && c.column == column && c.value == value;
            }
            return false;
        }

        public Cell getRelative(Util.Direction direction) throws IOException {
            switch (direction) {
                case UP: {
                    return getCell(row - 1, column);
                }

                case DOWN: {
                    return getCell(row + 1, column);
                }

                case LEFT: {
                    return getCell(row, column - 1);
                }

                case RIGHT: {
                    return getCell(row, column + 1);
                }
            }
            return null;
        }
        public String getTimeEST() throws IOException {
            return getCell(row, COLUMN_TIME).value.replaceAll("(.*) EST.*", "$1");
        }
        @Override
        public String toString() {
            return "Cell[row=" + row + ", column=" + column + ", string=" + value + "]";
        }
    }
}
