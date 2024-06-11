import psycopg2
from app_constants import AppConstants
from config_helper import ConfigHelper

if __name__ == '__main__':

    db_config = ConfigHelper.get_db_config_from_vault()
    with psycopg2.connect(
            host=db_config['host'],
            dbname=db_config['dbname'],
            user=db_config['username'],
            password=db_config['password'],
            port=db_config['port']
    ) as conn:
        with conn.cursor() as cur:
            with open('setup-tables.sql', 'r') as f:
                script = f.read()
                cur.execute(script)
                rows = cur.fetchall()
                # Print the results
                for row in rows:
                    print(row)
