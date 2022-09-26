# README

## File Bundling Notes

As of v5.0.11, any files in the `edu.iu.uits.lms.iuonly.*` packages will NOT be included in the test-jar bundle.

Only files in the `edu.iu.uits.lms.iuonly.jarexport` package will be included.

Configuration of this is handled by the `maven-jar-plugin` in the project's pom.xml.
