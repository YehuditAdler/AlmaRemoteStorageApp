## Installation

1. Clone this repository: `git clone https://github.com/jweisman/alma-blacklight.git`
2. Install dependencies: `bundle install`
3. Copy the `application.example.yml` file to `application.yml` and replace the placeholder values.
4. Run the rake task to populate the index: `rake oai_harvest`
5. Run the application: `bin\rails server` for WEBrick 
