package com.hotel.automation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * Generates Microsoft Word reports using Apache POI.
 * Creates formatted reports showing the lowest hotel prices for each hotel-city combination.
 */
public class ReportGenerator {
    private static final String DEFAULT_REPORT_NAME = "Hotel_Price_Analysis_Report.docx";
    
    /**
     * Generates a complete Word document report with analyzed hotel price data.
     * 
     * @param analysisResults Map of hotel-city combinations to their lowest prices
     * @param fileName Name of the output file
     * @return true if report was generated successfully, false otherwise
     */
    public boolean generateReport(Map<String, List<HotelPriceRecord>> analysisResults, String fileName) {
        try (XWPFDocument document = new XWPFDocument()) {
            // Add title
            addTitle(document, "Hotel Price Analysis Report");
            
            // Add generation date
            addParagraph(document, "Generated on: " + java.time.LocalDate.now().toString());
            addParagraph(document, ""); // Blank line
            
            // Add summary
            addSummary(document, analysisResults);
            
            // Add detailed results for each hotel-city combination
            addDetailedResults(document, analysisResults);
            
            // Write to file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                document.write(out);
                System.out.println("Report generated successfully: " + fileName);
                return true;
            }
            
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generates a report with the default filename.
     * 
     * @param analysisResults Map of hotel-city combinations to their lowest prices
     * @return true if report was generated successfully, false otherwise
     */
    public boolean generateReport(Map<String, List<HotelPriceRecord>> analysisResults) {
        return generateReport(analysisResults, DEFAULT_REPORT_NAME);
    }
    
    /**
     * Adds a formatted title to the document.
     * 
     * @param document XWPFDocument to add title to
     * @param titleText Text of the title
     */
    private void addTitle(XWPFDocument document, String titleText) {
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        
        XWPFRun titleRun = title.createRun();
        titleRun.setText(titleText);
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        titleRun.setFontFamily("Arial");
    }
    
    /**
     * Adds a regular paragraph to the document.
     * 
     * @param document XWPFDocument to add paragraph to
     * @param text Text content
     */
    private void addParagraph(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontFamily("Arial");
        run.setFontSize(11);
    }
    
    /**
     * Adds a heading to the document.
     * 
     * @param document XWPFDocument to add heading to
     * @param headingText Text of the heading
     */
    private void addHeading(XWPFDocument document, String headingText) {
        XWPFParagraph heading = document.createParagraph();
        heading.setStyle("Heading1");
        
        XWPFRun run = heading.createRun();
        run.setText(headingText);
        run.setBold(true);
        run.setFontSize(16);
        run.setFontFamily("Arial");
    }
    
    /**
     * Adds a subheading to the document.
     * 
     * @param document XWPFDocument to add subheading to
     * @param subheadingText Text of the subheading
     */
    private void addSubheading(XWPFDocument document, String subheadingText) {
        XWPFParagraph subheading = document.createParagraph();
        
        XWPFRun run = subheading.createRun();
        run.setText(subheadingText);
        run.setBold(true);
        run.setFontSize(14);
        run.setFontFamily("Arial");
        run.setColor("1F4E78"); // Dark blue color
    }
    
    /**
     * Adds a summary section to the report.
     * 
     * @param document XWPFDocument to add summary to
     * @param analysisResults Analysis results data
     */
    private void addSummary(XWPFDocument document, Map<String, List<HotelPriceRecord>> analysisResults) {
        addHeading(document, "Summary");
        addParagraph(document, "Total hotel-city combinations analyzed: " + analysisResults.size());
        addParagraph(document, "This report shows the 10 lowest-priced dates for each hotel in each city.");
        addParagraph(document, ""); // Blank line
    }
    
    /**
     * Adds detailed results for all hotel-city combinations.
     * 
     * @param document XWPFDocument to add results to
     * @param analysisResults Analysis results data
     */
    private void addDetailedResults(XWPFDocument document, Map<String, List<HotelPriceRecord>> analysisResults) {
        addHeading(document, "Detailed Analysis");
        addParagraph(document, ""); // Blank line
        
        // Sort keys for consistent ordering
        List<String> sortedKeys = analysisResults.keySet().stream()
                .sorted()
                .toList();
        
        for (String key : sortedKeys) {
            List<HotelPriceRecord> records = analysisResults.get(key);
            addHotelCitySection(document, key, records);
        }
    }
    
    /**
     * Adds a section for a specific hotel-city combination.
     * 
     * @param document XWPFDocument to add section to
     * @param hotelCityKey Hotel-city combination key
     * @param records List of price records
     */
    private void addHotelCitySection(XWPFDocument document, String hotelCityKey, 
                                      List<HotelPriceRecord> records) {
        // Add subheading for this hotel-city combination
        addSubheading(document, hotelCityKey);
        
        if (records.isEmpty()) {
            addParagraph(document, "No data available.");
            addParagraph(document, ""); // Blank line
            return;
        }
        
        // Create table for the results
        XWPFTable table = document.createTable(records.size() + 1, 3);
        table.setWidth("100%");
        
        // Set header row
        XWPFTableRow headerRow = table.getRow(0);
        styleHeaderCell(headerRow.getCell(0), "Rank");
        styleHeaderCell(headerRow.getCell(1), "Check-in Date");
        styleHeaderCell(headerRow.getCell(2), "Price (USD)");
        
        // Add data rows
        for (int i = 0; i < records.size(); i++) {
            HotelPriceRecord record = records.get(i);
            XWPFTableRow row = table.getRow(i + 1);
            
            styleDataCell(row.getCell(0), String.valueOf(i + 1));
            styleDataCell(row.getCell(1), record.getCheckinDate());
            styleDataCell(row.getCell(2), String.format("$%.2f", record.getPrice()));
        }
        
        // Add spacing after table
        addParagraph(document, "");
    }
    
    /**
     * Applies styling to a header cell.
     * 
     * @param cell Table cell to style
     * @param text Text content
     */
    private void styleHeaderCell(XWPFTableCell cell, String text) {
        cell.setColor("1F4E78"); // Dark blue background
        
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setColor("FFFFFF"); // White text
        run.setFontFamily("Arial");
        run.setFontSize(11);
    }
    
    /**
     * Applies styling to a data cell.
     * 
     * @param cell Table cell to style
     * @param text Text content
     */
    private void styleDataCell(XWPFTableCell cell, String text) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontFamily("Arial");
        run.setFontSize(10);
    }
    
    /**
     * Gets the default report filename.
     * 
     * @return Default filename
     */
    public static String getDefaultReportName() {
        return DEFAULT_REPORT_NAME;
    }
}
