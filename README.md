**JDK**:19

**Maven**:3.x

**Jetty**:11.0.15

**DB URL**: jdbc:h2:file:./data/guest;AUTO_SERVER=TRUE

**Endpoints**:

**1. GET/** - main page.

**2. GET/ comments** - Retrieve all comments (JSON)

**3. POST/ comments** - Add a new comment

**Parameters**: author (max. 64 characters), text (max. 1000 characters)
